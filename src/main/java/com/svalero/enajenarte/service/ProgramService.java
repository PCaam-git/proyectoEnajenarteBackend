package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.Program;
import com.svalero.enajenarte.domain.Speaker;
import com.svalero.enajenarte.domain.ProgramRegistration;
import com.svalero.enajenarte.dto.ProgramInDto;
import com.svalero.enajenarte.dto.ProgramOutDto;
import com.svalero.enajenarte.exception.DuplicateProgramException;
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

    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_CANCELLED = "CANCELLED";

    // POST
    public ProgramOutDto add(ProgramInDto programInDto) throws SpeakerNotFoundException, InvalidDateRangeException, DuplicateProgramException {
        Speaker speaker = speakerRepository.findById(programInDto.getSpeakerId())
                .orElseThrow(SpeakerNotFoundException::new);

        boolean duplicatedProgramExists = programRepository.findAll().stream()
                .anyMatch(program -> program.getName() != null
                        && program.getName().equalsIgnoreCase(programInDto.getName())
                        && program.getInitDate() != null
                        && program.getInitDate().equals(programInDto.getInitDate())
                        && (
                        program.isOnline() == programInDto.isOnline()
                                || (program.getSpeaker() != null
                                && program.getSpeaker().getId() == speaker.getId())
                ));

        if (duplicatedProgramExists) {
            throw new DuplicateProgramException();
        }

        Program program = modelMapper.map(programInDto, Program.class);

        validateProgramDatesAndStatus(program);
        program.setSpeaker(speaker);

        Program newProgram = programRepository.save(program);

        // Crea la entrada en el calendario con los datos del programa
        adminCalendarService.createEntryFromProgram(newProgram);

        ProgramOutDto programOutDto = modelMapper.map(newProgram, ProgramOutDto.class);
        if (newProgram.getSpeaker() != null) {
            programOutDto.setSpeakerId(newProgram.getSpeaker().getId());
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
                programsOutDtos.get(i).setSpeakerId(filteredPrograms.get(i).getSpeaker().getId());
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
            programOutDto.setSpeakerId(program.getSpeaker().getId());
            programOutDto.setSpeakerName(program.getSpeaker().getFirstName());
        }

        return programOutDto;
    }

    // PUT
    public ProgramOutDto modify(long id, ProgramInDto programInDto)
            throws ProgramNotFoundException, SpeakerNotFoundException, InvalidDateRangeException, DuplicateProgramException {

        Program existingProgram = programRepository.findById(id)
                .orElseThrow(ProgramNotFoundException::new);

        Speaker speaker = speakerRepository.findById(programInDto.getSpeakerId())
                .orElseThrow(SpeakerNotFoundException::new);

        boolean duplicatedProgramExists = programRepository.findAll().stream()
                .anyMatch(program -> program.getId() != id
                        && program.getName() != null
                        && program.getName().equalsIgnoreCase(programInDto.getName())
                        && program.getInitDate() != null
                        && program.getInitDate().equals(programInDto.getInitDate())
                        && (
                        program.isOnline() == programInDto.isOnline()
                                || (program.getSpeaker() != null
                                && program.getSpeaker().getId() == speaker.getId())
                ));

        if (duplicatedProgramExists) {
            throw new DuplicateProgramException();
        }

        modelMapper.map(programInDto, existingProgram);
        existingProgram.setId(id);
        existingProgram.setSpeaker(speaker);

        validateProgramDatesAndStatus(existingProgram);

        Program updatedProgram = programRepository.save(existingProgram);
        adminCalendarService.updateEntryFromProgram(updatedProgram);
        ProgramOutDto updatedProgramOutDto = modelMapper.map(updatedProgram, ProgramOutDto.class);

        if (updatedProgram.getSpeaker() != null) {
            updatedProgramOutDto.setSpeakerId(updatedProgram.getSpeaker().getId());
            updatedProgramOutDto.setSpeakerName(updatedProgram.getSpeaker().getFirstName());
        }

        return updatedProgramOutDto;
    }

    private void validateProgramDatesAndStatus(Program program) throws InvalidDateRangeException {
        String status = program.getStatus();

        if (status == null || status.isBlank()) {
            program.setStatus(STATUS_CONFIRMED);
            status = STATUS_CONFIRMED;
        }

        if (!STATUS_CONFIRMED.equals(status)
                && !STATUS_PENDING.equals(status)
                && !STATUS_CANCELLED.equals(status)) {
            throw new InvalidDateRangeException();
        }

        if (program.getInitDate() != null
                && program.getFinishDate() != null
                && program.getFinishDate().isBefore(program.getInitDate())) {
            throw new InvalidDateRangeException();
        }

        if (STATUS_CONFIRMED.equals(status) || STATUS_CANCELLED.equals(status)) {
            program.setConfirmationDeadline(null);
            return;
        }

        if (STATUS_PENDING.equals(status)) {
            if (program.getInitDate() != null
                    && program.getInitDate().isBefore(java.time.LocalDate.now())) {
                throw new InvalidDateRangeException();
            }

            if (program.getFinishDate() != null
                    && program.getFinishDate().isBefore(java.time.LocalDate.now())) {
                throw new InvalidDateRangeException();
            }

            if (program.getConfirmationDeadline() == null) {
                throw new InvalidDateRangeException();
            }

            if (program.getConfirmationDeadline().isBefore(java.time.LocalDate.now())) {
                throw new InvalidDateRangeException();
            }

            if (program.getInitDate() != null
                    && program.getConfirmationDeadline().isAfter(program.getInitDate())) {
                throw new InvalidDateRangeException();
            }
        }
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