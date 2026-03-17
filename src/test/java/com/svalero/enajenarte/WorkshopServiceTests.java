package com.svalero.enajenarte;

import com.svalero.enajenarte.domain.Speaker;
import com.svalero.enajenarte.domain.Workshop;
import com.svalero.enajenarte.dto.WorkshopInDto;
import com.svalero.enajenarte.dto.WorkshopOutDto;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.exception.WorkshopNotFoundException;
import com.svalero.enajenarte.repository.SpeakerRepository;
import com.svalero.enajenarte.repository.WorkshopRepository;
import com.svalero.enajenarte.service.WorkshopService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkshopServiceTests {

    @InjectMocks
    private WorkshopService workshopService;

    @Mock
    private WorkshopRepository workshopRepository;

    @Mock
    private SpeakerRepository speakerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() throws Exception {
        List<Workshop> mockWorkshopList = List.of(
                new Workshop(1L, "Oratoria básica", "Taller de oratoria y comunicación", LocalDate.of(2026, 2, 10), 90, 25, 20, true, null, null),
                new Workshop(2L, "Arte terapia", "Taller creativo para autocuidado", LocalDate.of(2026, 3, 5), 120, 30, 15, false, null, null)
        );

        List<WorkshopOutDto> modelMapperOut = List.of(
                new WorkshopOutDto(1L, "Oratoria básica", "Taller de oratoria y comunicación", LocalDate.of(2026, 2, 10), 90, 25, true, 1L),
                new WorkshopOutDto(2L, "Arte terapia", "Taller creativo para autocuidado", LocalDate.of(2026, 3, 5), 120, 30, false, 1L)
        );

        when(workshopRepository.findAll()).thenReturn(mockWorkshopList);
        when(modelMapper.map(mockWorkshopList, new TypeToken<List<WorkshopOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        List<WorkshopOutDto> actualWorkshopList = workshopService.findAll("", "", "");

        assertEquals(2, actualWorkshopList.size());
        assertEquals("Oratoria básica", actualWorkshopList.getFirst().getName());
        assertEquals("Arte terapia", actualWorkshopList.getLast().getName());

        verify(workshopRepository, times(1)).findAll();
        verify(workshopRepository, times(0)).findByNameContainingIgnoreCase("");
        verify(workshopRepository, times(0)).findByIsOnline(true);
    }

    @Test
    public void testFindAllByName() throws Exception {
        List<Workshop> mockWorkshopList = List.of(
                new Workshop(1L, "Arte terapia", "Taller creativo para autocuidado", LocalDate.of(2026, 3, 5), 120, 30, 15, false, null, null),
                new Workshop(2L, "Arte terapia avanzada", "Taller creativo avanzado", LocalDate.of(2026, 3, 20), 120, 35, 15, false, null, null)
        );

        List<WorkshopOutDto> modelMapperOut = List.of(
                new WorkshopOutDto(1L, "Arte terapia", "Taller creativo para autocuidado", LocalDate.of(2026, 3, 5), 120, 30, false, 1L),
                new WorkshopOutDto(2L, "Arte terapia avanzada", "Taller creativo avanzado", LocalDate.of(2026, 3, 20), 120, 35, false, 1L)
        );

        when(workshopRepository.findByNameContainingIgnoreCase("arte")).thenReturn(mockWorkshopList);
        when(modelMapper.map(mockWorkshopList, new TypeToken<List<WorkshopOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        List<WorkshopOutDto> actualWorkshopList = workshopService.findAll("arte", "", "");

        assertEquals(2, actualWorkshopList.size());
        assertEquals("Arte terapia", actualWorkshopList.getFirst().getName());
        assertEquals("Arte terapia avanzada", actualWorkshopList.getLast().getName());

        verify(workshopRepository, times(0)).findAll();
        verify(workshopRepository, times(1)).findByNameContainingIgnoreCase("arte");
    }

    @Test
    public void testFindAllByIsOnline() throws Exception {
        List<Workshop> mockWorkshopList = List.of(
                new Workshop(1L, "Oratoria básica", "Taller de oratoria", LocalDate.of(2026, 2, 10),
                        90, 25, 20, true, null, null),
                new Workshop(3L, "Coaching online", "Taller de coaching", LocalDate.of(2026, 3, 15),
                        120, 30, 15, true, null, null)
        );

        List<WorkshopOutDto> modelMapperWorkshopOutDto = List.of(
                new WorkshopOutDto(1L, "Oratoria básica", "Taller de oratoria",
                        LocalDate.of(2026, 2, 10), 90, 25, true, 1L),
                new WorkshopOutDto(3L, "Coaching online", "Taller de coaching",
                        LocalDate.of(2026, 3, 15), 120, 30, true, 1L)
        );

        when(workshopRepository.findByIsOnline(true)).thenReturn(mockWorkshopList);
        when(modelMapper.map(mockWorkshopList, new TypeToken<List<WorkshopOutDto>>() {}.getType()))
                .thenReturn(modelMapperWorkshopOutDto);

        List<WorkshopOutDto> actualWorkshopList = workshopService.findAll("", "true", "");

        assertEquals(2, actualWorkshopList.size());
        assertEquals("Oratoria básica", actualWorkshopList.getFirst().getName());
        assertEquals("Coaching online", actualWorkshopList.getLast().getName());

        verify(workshopRepository, times(0)).findAll();
        verify(workshopRepository, times(0)).findByNameContainingIgnoreCase(anyString());
        verify(workshopRepository, times(1)).findByIsOnline(true);
        verify(workshopRepository, times(0)).findBySpeaker(any(Speaker.class));
    }

    @Test
    public void testFindAllBySpeakerId() throws Exception {
        Speaker speaker = new Speaker();
        speaker.setId(5L);

        List<Workshop> mockWorkshopList = List.of(
                new Workshop(1L, "Oratoria básica", "Taller de oratoria", LocalDate.of(2026, 2, 10),
                        90, 25, 20, true, speaker, null),
                new Workshop(2L, "Oratoria avanzada", "Taller avanzado", LocalDate.of(2026, 3, 5),
                        120, 30, 15, false, speaker, null)
        );

        List<WorkshopOutDto> modelMapperWorkshopOutDto = List.of(
                new WorkshopOutDto(1L, "Oratoria básica", "Taller de oratoria",
                        LocalDate.of(2026, 2, 10), 90, 25, true, 5L),
                new WorkshopOutDto(2L, "Oratoria avanzada", "Taller avanzado",
                        LocalDate.of(2026, 3, 5), 120, 30, false, 5L)
        );

        when(speakerRepository.findById(5L)).thenReturn(Optional.of(speaker));
        when(workshopRepository.findBySpeaker(speaker)).thenReturn(mockWorkshopList);
        when(modelMapper.map(mockWorkshopList, new TypeToken<List<WorkshopOutDto>>() {}.getType()))
                .thenReturn(modelMapperWorkshopOutDto);

        List<WorkshopOutDto> actualWorkshopList = workshopService.findAll("", "", "5");

        assertEquals(2, actualWorkshopList.size());
        assertEquals("Oratoria básica", actualWorkshopList.getFirst().getName());
        assertEquals("Oratoria avanzada", actualWorkshopList.getLast().getName());

        verify(speakerRepository, times(1)).findById(5L);
        verify(workshopRepository, times(0)).findAll();
        verify(workshopRepository, times(0)).findByNameContainingIgnoreCase(anyString());
        verify(workshopRepository, times(0)).findByIsOnline(anyBoolean());
        verify(workshopRepository, times(1)).findBySpeaker(speaker);
    }

    @Test
    public void testFindAllBySpeakerId_SpeakerNotFound() {
        when(speakerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SpeakerNotFoundException.class, () ->
                workshopService.findAll("", "", "99"));

        verify(speakerRepository, times(1)).findById(99L);
        verify(workshopRepository, times(0)).findBySpeaker(any(Speaker.class));
    }

    @Test
    public void testFindById() throws WorkshopNotFoundException {
        Workshop workshop = new Workshop(7L, "Oratoria", "Taller de desarrollo",
                LocalDate.of(2026, 2, 10), 90, 25, 20, true, null, null);

        WorkshopOutDto workshopOutDto = new WorkshopOutDto(7L, "Oratoria", "Taller de desarrollo",
                LocalDate.of(2026, 2, 10), 90, 25, true, 1L);

        when(workshopRepository.findById(7L)).thenReturn(Optional.of(workshop));
        when(modelMapper.map(workshop, WorkshopOutDto.class)).thenReturn(workshopOutDto);

        WorkshopOutDto actualWorkshopOutDto = workshopService.findById(7L);

        assertNotNull(actualWorkshopOutDto);
        assertEquals(7L, actualWorkshopOutDto.getId());

        verify(workshopRepository, times(1)).findById(7L);
    }

    @Test
    public void testFindById_NotFound() {
        when(workshopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(WorkshopNotFoundException.class, () -> workshopService.findById(99L));

        verify(workshopRepository, times(1)).findById(99L);
    }

    @Test
    public void testAdd() throws SpeakerNotFoundException {
        WorkshopInDto workshopInDto = new WorkshopInDto("Oratoria", "Taller de desarrollo", LocalDate.of(2026, 2, 10), 90, 25, 20, true, 1L
        );

        Speaker speaker = new Speaker();
        speaker.setId(1L);

        Workshop workshop = new Workshop();
        Workshop savedWorkshop = new Workshop();
        savedWorkshop.setId(10L);

        WorkshopOutDto modelMapperOutDto = new WorkshopOutDto(10L, "Oratoria", "Taller de desarrollo",
                LocalDate.of(2026, 2, 10), 90, 25, true, 1L);

        when(speakerRepository.findById(1L)).thenReturn(Optional.of(speaker));
        when(modelMapper.map(workshopInDto, Workshop.class)).thenReturn(workshop);
        when(workshopRepository.save(workshop)).thenReturn(savedWorkshop);
        when(modelMapper.map(savedWorkshop, WorkshopOutDto.class)).thenReturn(modelMapperOutDto);

        WorkshopOutDto actualWorkshopOutDto = workshopService.add(workshopInDto);

        assertNotNull(actualWorkshopOutDto);
        assertEquals(10L, actualWorkshopOutDto.getId());

        verify(speakerRepository, times(1)).findById(1L);
        verify(workshopRepository, times(1)).save(workshop);
    }

    @Test
    public void testAdd_SpeakerNotFound() {
        WorkshopInDto workshopInDto = new WorkshopInDto("Oratoria", "Taller de desarrollo", LocalDate.of(2026, 2, 10), 90, 25, 20, true, 99L
        );

        when(speakerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SpeakerNotFoundException.class, () -> workshopService.add(workshopInDto));

        verify(speakerRepository, times(1)).findById(99L);
        verify(workshopRepository, times(0)).save(any(Workshop.class));
    }

    @Test
    public void testModify() throws WorkshopNotFoundException, SpeakerNotFoundException {
        long id = 5L;

        Workshop existingWorkshop = new Workshop();
        existingWorkshop.setId(id);

        Speaker speaker = new Speaker();
        speaker.setId(1L);

        WorkshopInDto workshopInDto = new WorkshopInDto("Oratoria actualizada", "Descripción actualizada", LocalDate.of(2026, 3, 10), 120, 30, 15, false, 1L
        );

        Workshop savedWorkshop = new Workshop();
        savedWorkshop.setId(id);

        WorkshopOutDto modelMapperOutDto = new WorkshopOutDto();
        modelMapperOutDto.setId(id);
        modelMapperOutDto.setName("Oratoria actualizada");

        when(workshopRepository.findById(id)).thenReturn(Optional.of(existingWorkshop));
        when(speakerRepository.findById(1L)).thenReturn(Optional.of(speaker));

        doNothing().when(modelMapper).map(workshopInDto, existingWorkshop);

        when(workshopRepository.save(existingWorkshop)).thenReturn(savedWorkshop);
        when(modelMapper.map(savedWorkshop, WorkshopOutDto.class)).thenReturn(modelMapperOutDto);

        WorkshopOutDto actualWorkshopOutDto = workshopService.modify(id, workshopInDto);

        assertNotNull(actualWorkshopOutDto);
        assertEquals(id, actualWorkshopOutDto.getId());
        assertEquals("Oratoria actualizada", actualWorkshopOutDto.getName());

        verify(workshopRepository, times(1)).findById(id);
        verify(speakerRepository, times(1)).findById(1L);
        verify(workshopRepository, times(1)).save(existingWorkshop);
    }

    @Test
    public void testModify_WorkshopNotFound() {
        long id = 5L;

        WorkshopInDto workshopInDto = new WorkshopInDto("Oratoria actualizada", "Descripción actualizada", LocalDate.of(2026, 3, 10), 120, 30, 15, false, 1L
        );

        when(workshopRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(WorkshopNotFoundException.class, () -> workshopService.modify(id, workshopInDto));

        verify(workshopRepository, times(1)).findById(id);
        verify(workshopRepository, times(0)).save(any(Workshop.class));
    }

    @Test
    public void testModify_SpeakerNotFound() {
        long id = 5L;

        Workshop existingWorkshop = new Workshop();
        existingWorkshop.setId(id);

        WorkshopInDto workshopInDto = new WorkshopInDto("Oratoria actualizada", "Descripción actualizada", LocalDate.of(2026, 3, 10), 120, 30, 15, false, 99L
        );

        when(workshopRepository.findById(id)).thenReturn(Optional.of(existingWorkshop));
        when(speakerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SpeakerNotFoundException.class, () -> workshopService.modify(id, workshopInDto));

        verify(workshopRepository, times(1)).findById(id);
        verify(speakerRepository, times(1)).findById(99L);
        verify(workshopRepository, times(0)).save(any(Workshop.class));
    }

    @Test
    public void testDelete() throws WorkshopNotFoundException {
        Workshop workshop = new Workshop();
        workshop.setId(1L);

        when(workshopRepository.findById(1L)).thenReturn(Optional.of(workshop));

        workshopService.delete(1L);

        verify(workshopRepository, times(1)).findById(1L);
        verify(workshopRepository, times(1)).delete(workshop);
    }

    @Test
    public void testDelete_NotFound() {
        when(workshopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(WorkshopNotFoundException.class, () -> workshopService.delete(99L));

        verify(workshopRepository, times(1)).findById(99L);
        verify(workshopRepository, times(0)).delete(any(Workshop.class));
    }
}
