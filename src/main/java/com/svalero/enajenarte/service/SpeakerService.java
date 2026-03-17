package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.Speaker;
import com.svalero.enajenarte.dto.SpeakerInDto;
import com.svalero.enajenarte.dto.SpeakerOutDto;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.repository.SpeakerRepository;
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
    public void delete(long id) throws SpeakerNotFoundException {
        Speaker speaker = speakerRepository.findById(id)
                .orElseThrow(SpeakerNotFoundException::new);
        speakerRepository.delete(speaker);
    }
}
