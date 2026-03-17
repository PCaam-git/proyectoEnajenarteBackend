package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.Speaker;
import com.svalero.enajenarte.domain.Workshop;
import com.svalero.enajenarte.dto.WorkshopInDto;
import com.svalero.enajenarte.dto.WorkshopOutDto;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.exception.WorkshopNotFoundException;
import com.svalero.enajenarte.repository.SpeakerRepository;
import com.svalero.enajenarte.repository.WorkshopRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkshopService {

    @Autowired
    private WorkshopRepository workshopRepository;
    @Autowired
    private SpeakerRepository speakerRepository;
    @Autowired
    private ModelMapper modelMapper;

    // POST
    public WorkshopOutDto add(WorkshopInDto workshopInDto) throws SpeakerNotFoundException {
        Speaker speaker = speakerRepository.findById(workshopInDto.getSpeakerId())
                .orElseThrow(SpeakerNotFoundException::new);

        Workshop workshop = modelMapper.map(workshopInDto, Workshop.class);
        workshop.setSpeaker(speaker);

        Workshop newWorkshop = workshopRepository.save(workshop);

        // Modificación aplicada: Mapear -> Setear IDs -> Devolver. Evita que speakerId salga a 0
        WorkshopOutDto workshopOutDto = modelMapper.map(newWorkshop, WorkshopOutDto.class);
        if (newWorkshop.getSpeaker() != null) {
            workshopOutDto.setSpeakerId(newWorkshop.getSpeaker().getId());
        }

        return workshopOutDto;
    }

    // DELETE
    public void delete(long id) throws WorkshopNotFoundException {
        Workshop workshop = workshopRepository.findById(id)
                .orElseThrow(WorkshopNotFoundException::new);

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
        }

        return workshopOutDto;
    }

    // PUT
    public WorkshopOutDto modify(long id, WorkshopInDto workshopInDto) throws WorkshopNotFoundException, SpeakerNotFoundException {
        Workshop existingWorkshop = workshopRepository.findById(id)
                .orElseThrow(WorkshopNotFoundException::new);
        Speaker speaker = speakerRepository.findById(workshopInDto.getSpeakerId())
                .orElseThrow(SpeakerNotFoundException::new);

        modelMapper.map(workshopInDto, existingWorkshop);
        existingWorkshop.setId(id);
        existingWorkshop.setSpeaker(speaker);

        Workshop updatedWorkshop = workshopRepository.save(existingWorkshop);
        WorkshopOutDto updatedWorkshopOutDto = modelMapper.map(updatedWorkshop, WorkshopOutDto.class);

        // Modificación aplicada: Mapear -> Setear IDs -> Devolver. Evita que speakerId salga a 0
        if (updatedWorkshop.getSpeaker() != null) {
            updatedWorkshopOutDto.setSpeakerId(updatedWorkshop.getSpeaker().getId());
        }

        return updatedWorkshopOutDto;
    }
}
