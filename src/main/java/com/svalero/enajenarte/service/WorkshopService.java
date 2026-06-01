package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.Registration;
import com.svalero.enajenarte.domain.Speaker;
import com.svalero.enajenarte.domain.Workshop;
import com.svalero.enajenarte.dto.WorkshopInDto;
import com.svalero.enajenarte.dto.WorkshopOutDto;
import com.svalero.enajenarte.exception.HasAssociatedRegistrationsException;
import com.svalero.enajenarte.exception.InvalidDateRangeException;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.exception.WorkshopNotFoundException;
import com.svalero.enajenarte.exception.DuplicateWorkshopException;
import com.svalero.enajenarte.repository.SpeakerRepository;
import com.svalero.enajenarte.repository.WorkshopRepository;
import com.svalero.enajenarte.repository.RegistrationRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkshopService {

    @Autowired
    private WorkshopRepository workshopRepository;
    @Autowired
    private SpeakerRepository speakerRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private AdminCalendarService adminCalendarService;

    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_CANCELLED = "CANCELLED";


    // POST
    public WorkshopOutDto add(WorkshopInDto workshopInDto) throws SpeakerNotFoundException, InvalidDateRangeException, DuplicateWorkshopException {
        Speaker speaker = speakerRepository.findById(workshopInDto.getSpeakerId())
                .orElseThrow(SpeakerNotFoundException::new);

        boolean duplicatedWorkshopExists = workshopRepository.findAll().stream()
                .anyMatch(existingWorkshop ->
                        existingWorkshop.getName() != null
                                && existingWorkshop.getName().equalsIgnoreCase(workshopInDto.getName())
                                && existingWorkshop.getStartDate() != null
                                && existingWorkshop.getStartDate().equals(workshopInDto.getStartDate())
                                && (
                                existingWorkshop.isOnline() == workshopInDto.isOnline()
                                        || (existingWorkshop.getSpeaker() != null
                                        && existingWorkshop.getSpeaker().getId() == speaker.getId())
                        )
                );

        if (duplicatedWorkshopExists) {
            throw new DuplicateWorkshopException();
        }

        Workshop workshop = modelMapper.map(workshopInDto, Workshop.class);
        validateWorkshopDatesAndStatus(workshop);

        workshop.setSpeaker(speaker);
        Workshop newWorkshop = workshopRepository.save(workshop);
        // Añade la entrada al calendario con los datos del workshop
        adminCalendarService.createEntryFromWorkshop(newWorkshop);

        // Modificación aplicada: Mapear -> Setear IDs -> Devolver. Evita que speakerId salga a 0
        WorkshopOutDto workshopOutDto = modelMapper.map(newWorkshop, WorkshopOutDto.class);
        if (newWorkshop.getSpeaker() != null) {
            workshopOutDto.setSpeakerId(newWorkshop.getSpeaker().getId());
            workshopOutDto.setSpeakerName(newWorkshop.getSpeaker().getFirstName() + " " + newWorkshop.getSpeaker().getLastName());
        }

        return workshopOutDto;
    }

    // DELETE
    public void delete(long id) throws WorkshopNotFoundException, HasAssociatedRegistrationsException {
        Workshop workshop = workshopRepository.findById(id)
                .orElseThrow(WorkshopNotFoundException::new);

        List<Registration> registrations = registrationRepository.findByWorkshop(workshop);
        if (!registrations.isEmpty()) {
            throw new HasAssociatedRegistrationsException();
        }

        adminCalendarService.deleteEntryFromWorkshop(workshop);
        workshopRepository.delete(workshop);
    }

    // GET ALL (con filtros)
    // Eliminada la excepción para permitir pruebas con los filtros
    public List<WorkshopOutDto> findAll(String name, String isOnline, String speakerId) {

        // Convertir parámetros a variables finales para el stream. Si el filtro no se usa, devuelve null. Si se usa, aplica el valor del filtro
        final String finalName = name.isEmpty() ? null : name.toLowerCase();
        final Boolean finalIsOnline = isOnline.isEmpty() ? null : Boolean.parseBoolean(isOnline);
        final Long finalSpeakerId = speakerId.isEmpty() ? null : Long.parseLong(speakerId);

        // Filtrado con Stream. Después de filtrar, lo convierte en lista
        List<Workshop> filteredWorkshops = workshopRepository.findAll().stream()
                .filter(workshop -> finalName == null || workshop.getName().toLowerCase().contains(finalName))
                .filter(workshop -> finalIsOnline == null || workshop.isOnline() == finalIsOnline)
                .filter(workshop -> finalSpeakerId == null || workshop.getSpeaker().getId() == finalSpeakerId)
                .toList();

        // Mapear DTOs
        List<WorkshopOutDto> workshopsOutDtos =
                modelMapper.map(filteredWorkshops, new TypeToken<List<WorkshopOutDto>>() {}.getType());

        // Setear IDs -> Devolver. Evita que speakerId salga a 0
        for (int i = 0; i < filteredWorkshops.size(); i++) {
            if (filteredWorkshops.get(i).getSpeaker() != null) {
                workshopsOutDtos.get(i).setSpeakerId(filteredWorkshops.get(i).getSpeaker().getId());
                workshopsOutDtos.get(i).setSpeakerName(filteredWorkshops.get(i).getSpeaker().getFirstName() + " " + filteredWorkshops.get(i).getSpeaker().getLastName());
            }
        }

        return workshopsOutDtos;
    }

    // GET by id
    public WorkshopOutDto findById(long id) throws WorkshopNotFoundException {
        Workshop workshop = workshopRepository.findById(id)
                .orElseThrow(WorkshopNotFoundException::new);

        WorkshopOutDto workshopOutDto = modelMapper.map(workshop, WorkshopOutDto.class);

        // Modificación aplicada: Mapear -> Setear IDs -> Devolver. Evita que speakerId salga a 0
        if (workshop.getSpeaker() != null) {
            workshopOutDto.setSpeakerId(workshop.getSpeaker().getId());
            workshopOutDto.setSpeakerName(workshop.getSpeaker().getFirstName() + " " + workshop.getSpeaker().getLastName());
        }

        return workshopOutDto;
    }

    // PUT
    public WorkshopOutDto modify(long id, WorkshopInDto workshopInDto) throws WorkshopNotFoundException, SpeakerNotFoundException, InvalidDateRangeException, DuplicateWorkshopException{
        Workshop existingWorkshop = workshopRepository.findById(id)
                .orElseThrow(WorkshopNotFoundException::new);
        Speaker speaker = speakerRepository.findById(workshopInDto.getSpeakerId())
                .orElseThrow(SpeakerNotFoundException::new);

        boolean duplicatedWorkshopExists = workshopRepository.findAll().stream()
                .anyMatch(workshop ->
                        workshop.getId() != id
                                && workshop.getName() != null
                                && workshop.getName().equalsIgnoreCase(workshopInDto.getName())
                                && workshop.getStartDate() != null
                                && workshop.getStartDate().equals(workshopInDto.getStartDate())
                                && (
                                workshop.isOnline() == workshopInDto.isOnline()
                                        || (workshop.getSpeaker() != null
                                        && workshop.getSpeaker().getId() == speaker.getId())
                        )
                );

        if (duplicatedWorkshopExists) {
            throw new DuplicateWorkshopException();
        }

        modelMapper.map(workshopInDto, existingWorkshop);
        existingWorkshop.setId(id);
        existingWorkshop.setSpeaker(speaker);

        validateWorkshopDatesAndStatus(existingWorkshop);

        Workshop updatedWorkshop = workshopRepository.save(existingWorkshop);
        adminCalendarService.updateEntryFromWorkshop(updatedWorkshop);
        WorkshopOutDto updatedWorkshopOutDto = modelMapper.map(updatedWorkshop, WorkshopOutDto.class);

        // Modificación aplicada: Mapear -> Setear IDs -> Devolver. Evita que speakerId salga a 0
        if (updatedWorkshop.getSpeaker() != null) {
            updatedWorkshopOutDto.setSpeakerId(updatedWorkshop.getSpeaker().getId());
            updatedWorkshopOutDto.setSpeakerName(updatedWorkshop.getSpeaker().getFirstName() + " " + updatedWorkshop.getSpeaker().getLastName());
        }

        return updatedWorkshopOutDto;
    }

    private void validateWorkshopDatesAndStatus(Workshop workshop) throws InvalidDateRangeException {
        String status = workshop.getStatus();

        if (status == null || status.isBlank()) {
            workshop.setStatus(STATUS_CONFIRMED);
            status = STATUS_CONFIRMED;
        }

        if (!STATUS_CONFIRMED.equals(status)
                && !STATUS_PENDING.equals(status)
                && !STATUS_CANCELLED.equals(status)) {
            throw new InvalidDateRangeException();
        }

        if ((STATUS_CONFIRMED.equals(status) || STATUS_CANCELLED.equals(status))) {
            workshop.setConfirmationDeadline(null);
            return;
        }

        if (STATUS_PENDING.equals(status)) {
            if (workshop.getStartDate() != null
                    && workshop.getStartDate().isBefore(java.time.LocalDate.now())) {
                throw new InvalidDateRangeException();
            }

            if (workshop.getConfirmationDeadline() == null) {
                throw new InvalidDateRangeException();
            }

            if (workshop.getConfirmationDeadline().isBefore(java.time.LocalDate.now())) {
                throw new InvalidDateRangeException();
            }

            if (workshop.getStartDate() != null
                    && workshop.getConfirmationDeadline().isAfter(workshop.getStartDate())) {
                throw new InvalidDateRangeException();
            }
        }
    }

    @Scheduled(cron = "0 0 * * * *") // se ejecuta cada hora
    public void cancelWorkshopsIfDeadlineExceeded() {

        List<Workshop> workshops = workshopRepository.findAll();

        for (Workshop workshop : workshops) {

            if (!workshop.isOnline()
                    && "PENDING".equals(workshop.getStatus())
                    && workshop.getConfirmationDeadline() != null
                    && workshop.getConfirmationDeadline().isBefore(java.time.LocalDate.now())) {

                List<Registration> registrations = registrationRepository.findByWorkshop(workshop);

                int totalParticipants = registrations.stream()
                        .mapToInt(Registration::getNumberOfTickets)
                        .sum();

                if (workshop.getMinimumParticipants() != null
                        && totalParticipants < workshop.getMinimumParticipants()) {

                    // Cancelar workshop
                    workshop.setStatus("CANCELLED");
                    workshopRepository.save(workshop);

                    // Cancelar inscripciones
                    for (Registration registration : registrations) {
                        registration.setStatus("CANCELLED");
                        registrationRepository.save(registration);

                        // Simulamos notificar al cliente
                        simulateWorkshopCancellationNotification(registration);
                    }
                }
            }
        }

    }

    private void simulateWorkshopCancellationNotification(Registration registration) {
        System.out.println("Simulando notificación de cancelación para la inscripción con código: "
                + registration.getConfirmationCode());
    }
}
