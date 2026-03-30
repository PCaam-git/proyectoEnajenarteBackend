package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.AdminCalendar;
import com.svalero.enajenarte.domain.Event;
import com.svalero.enajenarte.domain.Program;
import com.svalero.enajenarte.domain.Workshop;
import com.svalero.enajenarte.dto.AdminCalendarInDto;
import com.svalero.enajenarte.dto.AdminCalendarOutDto;
import com.svalero.enajenarte.exception.AdminCalendarNotFoundException;
import com.svalero.enajenarte.exception.InvalidDateRangeException;
import com.svalero.enajenarte.exception.InvalidStartDateTimeException;
import com.svalero.enajenarte.repository.AdminCalendarRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AdminCalendarService {

    @Autowired
    private AdminCalendarRepository adminCalendarRepository;
    @Autowired
    private ModelMapper modelMapper;

    // POST
    public AdminCalendarOutDto add(AdminCalendarInDto adminCalendarInDto) throws InvalidDateRangeException, InvalidStartDateTimeException {
        AdminCalendar adminCalendar = modelMapper.map(adminCalendarInDto, AdminCalendar.class);

        validateStartDateTime(adminCalendar);
        if (adminCalendar.getEndDate().isBefore(adminCalendar.getStartDate())) {
            throw new InvalidDateRangeException();
        }

        AdminCalendar newAdminCalendar = adminCalendarRepository.save(adminCalendar);
        return modelMapper.map(newAdminCalendar, AdminCalendarOutDto.class);
    }

    // GET ALL
    public List<AdminCalendarOutDto> findAll(String category, String speakerName) {

        final String finalCategory = category.isEmpty() ? null : category;
        final String finalSpeakerName = speakerName.isEmpty() ? null : speakerName.toLowerCase();

        List<AdminCalendar> filteredCalendarBlocks = adminCalendarRepository.findAll().stream()
                .filter(calendar -> finalCategory == null || calendar.getCategory().equals(finalCategory))
                .filter(calendar -> finalSpeakerName == null
                        || (calendar.getSpeakerName() != null
                        && calendar.getSpeakerName().toLowerCase().contains(finalSpeakerName)))
                .toList();

        return modelMapper.map(filteredCalendarBlocks, new TypeToken<List<AdminCalendarOutDto>>() {}.getType());
    }

    // GET BY ID
    public AdminCalendarOutDto findById(long id) throws AdminCalendarNotFoundException {
        AdminCalendar adminCalendar = adminCalendarRepository.findById(id)
                .orElseThrow(AdminCalendarNotFoundException::new);

        return modelMapper.map(adminCalendar, AdminCalendarOutDto.class);
    }

    // PUT
    public AdminCalendarOutDto modify(long id, AdminCalendarInDto adminCalendarInDto)
            throws AdminCalendarNotFoundException, InvalidDateRangeException, InvalidStartDateTimeException {

        AdminCalendar existingAdminCalendar = adminCalendarRepository.findById(id)
                .orElseThrow(AdminCalendarNotFoundException::new);

        modelMapper.map(adminCalendarInDto, existingAdminCalendar);
        existingAdminCalendar.setId(id);

        validateStartDateTime(existingAdminCalendar);
        if (existingAdminCalendar.getEndDate() != null
                && existingAdminCalendar.getStartDate() != null
                && existingAdminCalendar.getEndDate().isBefore(existingAdminCalendar.getStartDate())) {
            throw new InvalidDateRangeException();
        }

        AdminCalendar updatedAdminCalendar = adminCalendarRepository.save(existingAdminCalendar);
        return modelMapper.map(updatedAdminCalendar, AdminCalendarOutDto.class);
    }

    // DELETE
    public void delete(long id) throws AdminCalendarNotFoundException {
        AdminCalendar adminCalendar = adminCalendarRepository.findById(id)
                .orElseThrow(AdminCalendarNotFoundException::new);

        adminCalendarRepository.delete(adminCalendar);
    }

    // Entrada automática en el calendario al crear un workshop
    public void createEntryFromWorkshop(Workshop workshop) {

        // Busca en el calendario si ya existe una entrada vinculada a ese workshop,
        // identificándola por el identificador único "[WORKSHOP-id]" en el título
        List<AdminCalendar> existingEntries = adminCalendarRepository.findAll().stream()
                .filter(entry -> entry.getTitle() != null
                        && entry.getTitle().contains("[WORKSHOP-" + workshop.getId() + "]"))
                .toList();

        // Si ya existe una entrada para ese workshop, no la crea para evitar un duplicado
        if (!existingEntries.isEmpty()) {
            return;
        }

        // Construye una nueva entrada de calendario con los datos del workshop
        AdminCalendar adminCalendar = AdminCalendar.builder()
                // El título incluye el identificador único para poder localizarla después
                .title("[WORKSHOP-" + workshop.getId() + "] " + workshop.getName())
                .startDate(workshop.getStartDate())
                .endDate(workshop.getStartDate()) // Workshop no tiene endDate, se usa startDate
                .hour(workshop.getHour())
                .durationMinutes(workshop.getDurationMinutes())
                .category("WORKSHOP")
                .description(workshop.getDescription())
                // Si el workshop tiene ponente asignado, se guarda su nombre completo;
                // en caso contrario, se deja el campo como null
                .speakerName(workshop.getSpeaker() != null
                        ? workshop.getSpeaker().getFirstName() + " " + workshop.getSpeaker().getLastName()
                        : null)
                .build();

        // Guarda la entrada en el repositorio de calendario
        adminCalendarRepository.save(adminCalendar);
    }

    // Actualización automática de la entrada en el calendario al modificar un workshop
    public void updateEntryFromWorkshop(Workshop workshop) {

        // Busca la entrada del calendario vinculada a este workshop,
        // identificándola por el identificador único "[WORKSHOP-id]" en el título
        AdminCalendar existingEntry = adminCalendarRepository.findAll().stream()
                .filter(entry -> entry.getTitle() != null
                        && entry.getTitle().contains("[WORKSHOP-" + workshop.getId() + "]"))
                .findFirst()
                .orElse(null);

        // Si no existe una entrada vinculada a este workshop, llama a createEntryFromWorkshop para crear una nueva
        if (existingEntry == null) {
            createEntryFromWorkshop(workshop);
            return;
        }

        // Actualiza los campos de la entrada existente con los datos actuales del workshop
        existingEntry.setTitle("[WORKSHOP-" + workshop.getId() + "] " + workshop.getName());
        existingEntry.setStartDate(workshop.getStartDate());
        existingEntry.setEndDate(workshop.getStartDate()); // Workshop no tiene endDate, se usa startDate
        existingEntry.setHour(workshop.getHour());
        existingEntry.setDurationMinutes(workshop.getDurationMinutes());
        existingEntry.setCategory("WORKSHOP");
        existingEntry.setDescription(workshop.getDescription());
        // Si el workshop tiene ponente asignado, se guarda su nombre completo;
        // en caso contrario, se deja el campo como null
        existingEntry.setSpeakerName(workshop.getSpeaker() != null
                ? workshop.getSpeaker().getFirstName() + " " + workshop.getSpeaker().getLastName()
                : null);

        // Guarda la entrada actualizada en el repositorio de calendario
        adminCalendarRepository.save(existingEntry);
    }

    // Entrada automática en el calendario al crear un programa
    public void createEntryFromProgram(Program program) {
        // Busca en el calendario si ya existe una entrada vinculada a ese programa identificándola con el identificador
        // único "[PROGRAM-id]" en el título
        List<AdminCalendar> existingEntries = adminCalendarRepository.findAll().stream()
                .filter(entry -> entry.getTitle() != null
                        && entry.getTitle().contains("[PROGRAM-" + program.getId() + "]"))
                .toList();

        // Si ya existe una entrada para ese programa, no la crea para evitar un duplicado.
        if (!existingEntries.isEmpty()) {
            return;
        }

//        Construye una nueva entrada de calendario con los datos del programa
        AdminCalendar adminCalendar = AdminCalendar.builder()
                // El título incluye el identificador único para poder localizarla después
                .title("[PROGRAM-" + program.getId() + "] " + program.getName())
                .startDate(program.getInitDate())
                .endDate(program.getFinishDate())
                .hour(program.getHour())
                .durationMinutes(program.getDurationMinutes())
                .category("PROGRAM")
                .description(program.getDescription())
                // Si el programa tiene un ponente asignado, se guarda su nombre completo,
                // si no tiene ponente asignado, se guarda null
                .speakerName(program.getSpeaker() != null
                        ? program.getSpeaker().getFirstName() + " " + program.getSpeaker().getLastName()
                        : null)
                .build();

        // Guarda la entrada en el repositorio de calendario
        adminCalendarRepository.save(adminCalendar);
    }

    // Actualiza automáticamente la entrada en el calendario al modificar un programa
    public void updateEntryFromProgram(Program program) {

        // Busca la entrada del calendario vinculada a este programa mediante el identificador
        // único "[PROGRAM-id]" en el título
        AdminCalendar existingEntry = adminCalendarRepository.findAll().stream()
                .filter(entry -> entry.getTitle() != null
                        && entry.getTitle().contains("[PROGRAM-" + program.getId() + "]"))
                .findFirst()
                .orElse(null);

        // Si no existe una entrada vinculada a este programa, llama a createEntryFromProgram para crear una nueva
        if (existingEntry == null) {
            createEntryFromProgram(program);
            return;
        }

        // Actualiza los campos de la entrada existente con los datos actuales del programa
        existingEntry.setTitle("[PROGRAM-" + program.getId() + "] " + program.getName());
        existingEntry.setStartDate(program.getInitDate());
        existingEntry.setEndDate(program.getFinishDate());
        existingEntry.setHour(program.getHour());
        existingEntry.setDurationMinutes(program.getDurationMinutes());
        existingEntry.setCategory("PROGRAM");
        existingEntry.setDescription(program.getDescription());
        // Si el programa tiene ponente asignado, se guarda su nombre completo;
        // en caso contrario, se deja el campo como null
        existingEntry.setSpeakerName(program.getSpeaker() != null
                ? program.getSpeaker().getFirstName() + " " + program.getSpeaker().getLastName()
                : null);

        // Guarda la entrada actualizada en el repositorio de calendario
        adminCalendarRepository.save(existingEntry);
    }

    // Entrada automática en el calendario al crear un evento
    public void createEntryFromEvent(Event event) {
        AdminCalendar adminCalendar = AdminCalendar.builder()
                .title(event.getTitle())
                .startDate(event.getEventDate().toLocalDate())
                .endDate(event.getEventDate().toLocalDate())
                .hour(event.getEventDate().toLocalTime().toString())
                .durationMinutes(60)
                .category("EVENT")
                .description(event.getLocation())
                .speakerName(event.getSpeaker() != null
                        ? event.getSpeaker().getFirstName() + " " + event.getSpeaker().getLastName()
                        : null)
                .build();

        adminCalendarRepository.save(adminCalendar);
    }

    private void validateStartDateTime(AdminCalendar adminCalendar) throws InvalidStartDateTimeException {
        LocalDate today = LocalDate.now();

        if (adminCalendar.getStartDate().isBefore(today)) {
            throw new InvalidStartDateTimeException("startDate must be in the future");
        }

        if (adminCalendar.getStartDate().isEqual(today)) {
            LocalTime eventTime = LocalTime.parse(adminCalendar.getHour());
            LocalTime now = LocalTime.now();

            if (!eventTime.isAfter(now)) {
                throw new InvalidStartDateTimeException("startDate must be in the future");
            }
        }
    }
}