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
        AdminCalendar adminCalendar = AdminCalendar.builder()
                .title(workshop.getName())
                .startDate(workshop.getStartDate())
                .endDate(workshop.getStartDate())
                .hour(workshop.getHour())
                .durationMinutes(workshop.getDurationMinutes())
                .category("WORKSHOP")
                .description(workshop.getDescription())
                .speakerName(workshop.getSpeaker() != null
                ? workshop.getSpeaker().getFirstName() + " " + workshop.getSpeaker().getLastName()
                        : null)
                .build();

        adminCalendarRepository.save(adminCalendar);
    }

    // Entrada automática en el calendario al crear un programa
    public void createEntryFromProgram(Program program) {
        AdminCalendar adminCalendar = AdminCalendar.builder()
                .title(program.getName())
                .startDate(program.getInitDate())
                .endDate(program.getFinishDate())
                .hour(program.getHour())
                .durationMinutes(program.getDurationMinutes())
                .category("PROGRAM")
                .description(program.getDescription())
                .speakerName(program.getSpeaker() != null
                        ? program.getSpeaker().getFirstName() + " " + program.getSpeaker().getLastName()
                        : null)
                .build();

        adminCalendarRepository.save(adminCalendar);
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