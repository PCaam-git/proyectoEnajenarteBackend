package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.Program;
import com.svalero.enajenarte.domain.Speaker;
import com.svalero.enajenarte.domain.ProgramRegistration;
import com.svalero.enajenarte.dto.ProgramInDto;
import com.svalero.enajenarte.dto.ProgramOutDto;
import com.svalero.enajenarte.exception.InvalidDateRangeException;
import com.svalero.enajenarte.exception.ProgramNotFoundException;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.repository.ProgramRepository;
import com.svalero.enajenarte.repository.SpeakerRepository;
import com.svalero.enajenarte.repository.ProgramRegistrationRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Service
public class ProgramService {

    @Autowired
    private ProgramRepository programRepository;
    @Autowired
    private ProgramRegistrationRepository programRegistrationRepository;
    @Autowired
    private SpeakerRepository speakerRepository;
    @Autowired
    private AdminCalendarService adminCalendarService;
    @Autowired
    private ModelMapper modelMapper;

    // POST
    public ProgramOutDto add(ProgramInDto programInDto) throws SpeakerNotFoundException, InvalidDateRangeException {
        Speaker speaker = speakerRepository.findById(programInDto.getSpeakerId())
                .orElseThrow(SpeakerNotFoundException::new);

        Program program = modelMapper.map(programInDto, Program.class);

        // confirmationDeadline debe ser anterior a initDate
        if (program.getConfirmationDeadline().isAfter(program.getInitDate())) {
            throw new InvalidDateRangeException();
        }

        if (program.isOnline()) {
            program.setStatus("CONFIRMED");
        } else {
            program.setStatus("PENDING");
        }

        program.setSpeaker(speaker);

        Program newProgram = programRepository.save(program);

        // Crea la entrada en el calendario con los datos del programa
        adminCalendarService.createEntryFromProgram(newProgram);

        ProgramOutDto programOutDto = modelMapper.map(newProgram, ProgramOutDto.class);
        if (newProgram.getSpeaker() != null) {
            programOutDto.setSpeakerName(newProgram.getSpeaker().getFirstName());
        }

        return programOutDto;
    }

    // DELETE
    public void delete(long id) throws ProgramNotFoundException {
        Program program = programRepository.findById(id)
                .orElseThrow(ProgramNotFoundException::new);

        programRepository.delete(program);
    }

    // GET ALL
    public List<ProgramOutDto> findAll(String name, String location, String isOnline) {

        final String finalName = name.isEmpty() ? null : name.toLowerCase();
        final String finalLocation = location.isEmpty() ? null : location.toLowerCase();
        final Boolean finalIsOnline = isOnline.isEmpty() ? null : Boolean.parseBoolean(isOnline);


        List<Program> filteredPrograms = programRepository.findAll().stream()
                .filter(program -> finalName == null || program.getName().toLowerCase().contains(finalName))
                .filter(program -> finalLocation == null || program.getLocation().toLowerCase().contains(finalLocation))
                .filter(program -> finalIsOnline == null || program.isOnline() == finalIsOnline)

                .toList();

        List<ProgramOutDto> programsOutDtos =
                modelMapper.map(filteredPrograms, new TypeToken<List<ProgramOutDto>>() {}.getType());

        // Setear IDs -> Devolver. Evita que speakerId salga a 0
        for (int i = 0; i < filteredPrograms.size(); i++) {
            if (filteredPrograms.get(i).getSpeaker() != null) {
                programsOutDtos.get(i).setSpeakerName(filteredPrograms.get(i).getSpeaker().getFirstName());
            }
        }
        return programsOutDtos;
    }

    // GET by id
    public ProgramOutDto findById(long id) throws ProgramNotFoundException {
        Program program = programRepository.findById(id)
                .orElseThrow(ProgramNotFoundException::new);

        ProgramOutDto programOutDto = modelMapper.map(program, ProgramOutDto.class);

        if (program.getSpeaker() != null) {
            programOutDto.setSpeakerName(program.getSpeaker().getFirstName());
        }

        return programOutDto;
    }

    // PUT
    public ProgramOutDto modify(long id, ProgramInDto programInDto)
            throws ProgramNotFoundException, SpeakerNotFoundException, InvalidDateRangeException {

        Program existingProgram = programRepository.findById(id)
                .orElseThrow(ProgramNotFoundException::new);

        Speaker speaker = speakerRepository.findById(programInDto.getSpeakerId())
                .orElseThrow(SpeakerNotFoundException::new);

        String status = existingProgram.getStatus();

        modelMapper.map(programInDto, existingProgram);
        existingProgram.setId(id);
        existingProgram.setSpeaker(speaker);

        if (existingProgram.getConfirmationDeadline() != null
                && existingProgram.getInitDate() != null
                && existingProgram.getConfirmationDeadline().isAfter(existingProgram.getInitDate())) {
            throw new InvalidDateRangeException();
        }

        if (existingProgram.isOnline()) {
            if (!"CANCELLED".equals(status)) {
                existingProgram.setStatus("CONFIRMED");
            } else {
                existingProgram.setStatus(status);
            }
        } else {
            existingProgram.setStatus(status);
        }

        Program updatedProgram = programRepository.save(existingProgram);

        ProgramOutDto updatedProgramOutDto = modelMapper.map(updatedProgram, ProgramOutDto.class);

        if (updatedProgram.getSpeaker() != null) {
            updatedProgramOutDto.setSpeakerName(updatedProgram.getSpeaker().getFirstName());
        }

        return updatedProgramOutDto;
    }

    @Scheduled(cron = "0 0 * * * *") // se ejecuta cada hora
    public void cancelProgramsIfDeadlineExceeded() {

        List<Program> programs = programRepository.findAll();

        for (Program program : programs) {

            if ("PENDING".equals(program.getStatus())
                    && program.getConfirmationDeadline() != null
                    && program.getConfirmationDeadline().isBefore(java.time.LocalDate.now())) {

                List<ProgramRegistration> registrations = programRegistrationRepository.findByProgram(program);

                int totalParticipants = registrations.stream()
                        .mapToInt(ProgramRegistration::getNumberOfTickets)
                        .sum();

                if (program.getMinimumParticipants() != null
                        && totalParticipants < program.getMinimumParticipants()) {

                    // Cancelar programa
                    program.setStatus("CANCELLED");
                    programRepository.save(program);

                    // Cancelar inscripciones
                    for (ProgramRegistration registration : registrations) {
                        registration.setStatus("CANCELLED");
                        programRegistrationRepository.save(registration);

                        // Simulamos notificar al cliente
                        simulateProgramCancellationNotification(registration);
                    }
                }
            }
        }
    }

    private void simulateProgramCancellationNotification(ProgramRegistration registration) {
        System.out.println(registration.getUser().getFullName()
                + " , el programa se ha cancelado. Te informaremos cuando haya una nueva convocatoria.");
    }
}