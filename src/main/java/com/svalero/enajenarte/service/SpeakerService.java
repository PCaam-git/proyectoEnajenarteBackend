package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.Speaker;
import com.svalero.enajenarte.domain.Workshop;
import com.svalero.enajenarte.domain.Program;
import com.svalero.enajenarte.domain.Event;
import com.svalero.enajenarte.dto.SpeakerInDto;
import com.svalero.enajenarte.dto.SpeakerOutDto;
import com.svalero.enajenarte.exception.HasAssociatedRegistrationsException;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.repository.SpeakerRepository;
import com.svalero.enajenarte.repository.WorkshopRepository;
import com.svalero.enajenarte.repository.ProgramRepository;
import com.svalero.enajenarte.repository.EventRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpeakerService {

    @Autowired
    public SpeakerRepository speakerRepository;
    @Autowired
    public WorkshopRepository workshopRepository;
    @Autowired
    public ProgramRepository programRepository;
    @Autowired
    public EventRepository eventRepository;
    @Autowired
    public ModelMapper modelMapper;

    // GET (con filtros simultáneos
    public List<SpeakerOutDto> findAll(String speciality, String available, String yearsExperience) {
        final String finalSpeciality = speciality.isEmpty() ? null : speciality.toLowerCase();
        final Boolean finalAvailable = available.isEmpty() ? null : Boolean.parseBoolean(available);
        final Integer finalYearsExperience = yearsExperience.isEmpty() ? null : Integer.parseInt(yearsExperience);

        List<Speaker> filteredSpeakers = speakerRepository.findAll().stream()
                .filter(speaker -> finalSpeciality == null || speaker.getSpeciality().toLowerCase().contains(finalSpeciality))
                .filter(speaker -> finalAvailable == null || speaker.isAvailable() == finalAvailable)
                .filter(speaker -> finalYearsExperience == null || speaker.getYearsExperience() == finalYearsExperience)
                .toList();

        // Mapear a DTOs
        List<SpeakerOutDto> speakerOutDtoList =
                modelMapper.map(filteredSpeakers, new TypeToken<List<SpeakerOutDto>>() {
                }.getType());

       return speakerOutDtoList;
}



    // GET by ID
    public SpeakerOutDto findById(long id) throws SpeakerNotFoundException {
        Speaker speaker = speakerRepository.findById(id)
                .orElseThrow(SpeakerNotFoundException::new);

        return modelMapper.map(speaker, SpeakerOutDto.class);
    }

    // POST
    public SpeakerOutDto add(SpeakerInDto speakerInDto) {
        Speaker speaker= modelMapper.map(speakerInDto, Speaker.class);

        // generado por el sistema
        speaker.setWorkshopHoursTotal(0);

        Speaker newSpeaker = speakerRepository.save(speaker);
        return modelMapper.map(newSpeaker, SpeakerOutDto.class);
    }

    // PUT
    public SpeakerOutDto modify(long id, SpeakerInDto speakerInDto) throws SpeakerNotFoundException {
        Speaker existingSpeaker = speakerRepository.findById(id)
                .orElseThrow(SpeakerNotFoundException::new);

        float currentHours = existingSpeaker.getWorkshopHoursTotal();

        modelMapper.map(speakerInDto, existingSpeaker);
        existingSpeaker.setId(id);
        existingSpeaker.setWorkshopHoursTotal(currentHours);

        Speaker updateSpeaker = speakerRepository.save(existingSpeaker);
        return modelMapper.map(updateSpeaker, SpeakerOutDto.class);
    }

    // DELETE
    // No se puede eliminar un ponente con actividades asociadas
    public void delete(long id) throws SpeakerNotFoundException, HasAssociatedRegistrationsException {
        Speaker speaker = speakerRepository.findById(id)
                .orElseThrow(SpeakerNotFoundException::new);

        List<Workshop> workshops = workshopRepository.findBySpeaker(speaker);
        if (!workshops.isEmpty()) {
            throw new HasAssociatedRegistrationsException();
        }

        List<Program> programs = programRepository.findBySpeaker(speaker);
        if (!programs.isEmpty()) {
            throw new HasAssociatedRegistrationsException();
        }

        List<Event> events = eventRepository.findAll().stream()
                .filter(event -> event.getSpeaker() != null
                        && event.getSpeaker().getId() == speaker.getId())
                .toList();

        if (!events.isEmpty()) {
            throw new HasAssociatedRegistrationsException();
        }

        speakerRepository.delete(speaker);
    }
}
