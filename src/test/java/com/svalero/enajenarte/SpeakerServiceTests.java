package com.svalero.enajenarte;

import com.svalero.enajenarte.domain.Speaker;
import com.svalero.enajenarte.dto.SpeakerInDto;
import com.svalero.enajenarte.dto.SpeakerOutDto;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.repository.SpeakerRepository;
import com.svalero.enajenarte.service.SpeakerService;
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
public class SpeakerServiceTests {

    @InjectMocks
    private SpeakerService speakerService;

    @Mock
    private SpeakerRepository speakerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() {
        List<Speaker> mockSpeakerList = List.of(
                new Speaker(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5, 0, true, LocalDate.of(2025, 1, 1), null),
                new Speaker(2L, "Carlos", "Perez", "carlos@mail.com", "oratoria", 8, 0, true, LocalDate.of(2024, 5, 10), null)
        );

        List<SpeakerOutDto> modelMapperOut = List.of(
                new SpeakerOutDto(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5),
                new SpeakerOutDto(2L, "Carlos", "Perez", "carlos@mail.com", "oratoria", 8)
        );

        when(speakerRepository.findAll()).thenReturn(mockSpeakerList);
        when(modelMapper.map(mockSpeakerList, new TypeToken<List<SpeakerOutDto>>() {}.getType()))
                .thenReturn(modelMapperOut);

        List<SpeakerOutDto> actualSpeakerList = speakerService.findAll("", "", "");

        assertEquals(2, actualSpeakerList.size());
        assertEquals("Ana", actualSpeakerList.getFirst().getFirstName());
        assertEquals("Carlos", actualSpeakerList.getLast().getFirstName());

        verify(speakerRepository, times(1)).findAll();
        verify(speakerRepository, times(0)).findBySpecialityContainingIgnoreCase("");
        verify(speakerRepository, times(0)).findByAvailable(true);
        verify(speakerRepository, times(0)).findByYearsExperience(5);
    }

    @Test
    public void testFindAllBySpeciality() {
        List<Speaker> mockSpeakerList = List.of(
                new Speaker(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5, 0, true, LocalDate.of(2025, 1, 1), null),
                new Speaker(2L, "Lucia", "Martin", "lucia@mail.com", "mindfulness", 3, 0, true, LocalDate.of(2025, 2, 1), null)
        );

        List<SpeakerOutDto> modelMapperOut = List.of(
                new SpeakerOutDto(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5),
                new SpeakerOutDto(2L, "Lucia", "Martin", "lucia@mail.com", "mindfulness", 3)
        );

        when(speakerRepository.findBySpecialityContainingIgnoreCase("mind")).thenReturn(mockSpeakerList);
        when(modelMapper.map(mockSpeakerList, new TypeToken<List<SpeakerOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        List<SpeakerOutDto> actualSpeakerList = speakerService.findAll("mind", "", "");

        assertEquals(2, actualSpeakerList.size());
        assertEquals("Ana", actualSpeakerList.getFirst().getFirstName());
        assertEquals("Lucia", actualSpeakerList.getLast().getFirstName());

        verify(speakerRepository, times(0)).findAll();
        verify(speakerRepository, times(1)).findBySpecialityContainingIgnoreCase("mind");
    }

    @Test
    public void testFindAllByAvailable() {
        List<Speaker> mockSpeakerList = List.of(
                new Speaker(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5, 0, true,
                        LocalDate.of(2025, 1, 1), null),
                new Speaker(3L, "Lucia", "Martin", "lucia@mail.com", "oratoria", 3, 0, true,
                        LocalDate.of(2025, 2, 1), null)
        );

        List<SpeakerOutDto> modelMapperSpeakerOutDto = List.of(
                new SpeakerOutDto(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5),
                new SpeakerOutDto(3L, "Lucia", "Martin", "lucia@mail.com", "oratoria", 3)
        );

        when(speakerRepository.findByAvailable(true)).thenReturn(mockSpeakerList);
        when(modelMapper.map(mockSpeakerList, new TypeToken<List<SpeakerOutDto>>() {}.getType()))
                .thenReturn(modelMapperSpeakerOutDto);

        List<SpeakerOutDto> actualSpeakerList = speakerService.findAll("", "true", "");

        assertEquals(2, actualSpeakerList.size());
        assertEquals("Ana", actualSpeakerList.getFirst().getFirstName());
        assertEquals("Lucia", actualSpeakerList.getLast().getFirstName());

        verify(speakerRepository, times(0)).findAll();
        verify(speakerRepository, times(0)).findBySpecialityContainingIgnoreCase(anyString());
        verify(speakerRepository, times(1)).findByAvailable(true);
        verify(speakerRepository, times(0)).findByYearsExperience(anyInt());
    }

    @Test
    public void testFindAllByYearsExperience() {
        List<Speaker> mockSpeakerList = List.of(
                new Speaker(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5, 0, true,
                        LocalDate.of(2025, 1, 1), null),
                new Speaker(2L, "Pedro", "Sanchez", "pedro@mail.com", "coaching", 5, 0, true,
                        LocalDate.of(2024, 3, 10), null)
        );

        List<SpeakerOutDto> modelMapperSpeakerOutDto = List.of(
                new SpeakerOutDto(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5),
                new SpeakerOutDto(2L, "Pedro", "Sanchez", "pedro@mail.com", "coaching", 5)
        );

        when(speakerRepository.findByYearsExperience(5)).thenReturn(mockSpeakerList);
        when(modelMapper.map(mockSpeakerList, new TypeToken<List<SpeakerOutDto>>() {}.getType()))
                .thenReturn(modelMapperSpeakerOutDto);

        List<SpeakerOutDto> actualSpeakerList = speakerService.findAll("", "", "5");

        assertEquals(2, actualSpeakerList.size());
        assertEquals("Ana", actualSpeakerList.getFirst().getFirstName());
        assertEquals("Pedro", actualSpeakerList.getLast().getFirstName());

        verify(speakerRepository, times(0)).findAll();
        verify(speakerRepository, times(0)).findBySpecialityContainingIgnoreCase(anyString());
        verify(speakerRepository, times(0)).findByAvailable(anyBoolean());
        verify(speakerRepository, times(1)).findByYearsExperience(5);
    }

    @Test
    public void testFindById() throws SpeakerNotFoundException {
        Speaker speaker = new Speaker(7L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5, 0, true, LocalDate.of(2025, 1, 1), null
        );

        SpeakerOutDto speakerOutDto = new SpeakerOutDto(7L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5);

        when(speakerRepository.findById(7L)).thenReturn(Optional.of(speaker));
        when(modelMapper.map(speaker, SpeakerOutDto.class)).thenReturn(speakerOutDto);

        SpeakerOutDto result = speakerService.findById(7L);

        assertNotNull(result);
        assertEquals(7L, result.getId());

        verify(speakerRepository, times(1)).findById(7L);
    }

    @Test
    public void testFindById_NotFound() {
        when(speakerRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(SpeakerNotFoundException.class, () -> speakerService.findById(7L));

        verify(speakerRepository, times(1)).findById(7L);
    }

    @Test
    public void testAdd() {
        SpeakerInDto speakerInDto = new SpeakerInDto("Ana", "Lopez", "ana@mail.com", "mindfulness", 5, 10f, true, LocalDate.of(2025, 1, 1)
        );

        Speaker mappedSpeaker = new Speaker(0L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5, 10, true, LocalDate.of(2025, 1, 1), null
        );

        Speaker savedSpeaker = new Speaker(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5, 0, true, LocalDate.of(2025, 1, 1), null
        );

        SpeakerOutDto speakerOutDto = new SpeakerOutDto(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5);

        when(modelMapper.map(speakerInDto, Speaker.class)).thenReturn(mappedSpeaker);
        when(speakerRepository.save(mappedSpeaker)).thenReturn(savedSpeaker);
        when(modelMapper.map(savedSpeaker, SpeakerOutDto.class)).thenReturn(speakerOutDto);

        SpeakerOutDto result = speakerService.add(speakerInDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ana", result.getFirstName());

        // El sistema fuerza workshopHoursTotal = 0
        assertEquals(0, mappedSpeaker.getWorkshopHoursTotal());

        verify(speakerRepository, times(1)).save(mappedSpeaker);
    }

    @Test
    public void testModify() throws SpeakerNotFoundException {
        long id = 5L;

        Speaker existingSpeaker = new Speaker(id, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5, 15, true, LocalDate.of(2025, 1, 1), null
        );

        SpeakerInDto speakerInDto = new SpeakerInDto("AnaUpdated", "LopezUpdated", "ana.updated@mail.com", "oratoria", 8, 999f, false, LocalDate.of(2025, 1, 1)
        );

        SpeakerOutDto speakerOutDto = new SpeakerOutDto(id, "AnaUpdated", "LopezUpdated", "ana.updated@mail.com", "oratoria", 8);

        when(speakerRepository.findById(id)).thenReturn(Optional.of(existingSpeaker));

        doNothing().when(modelMapper).map(speakerInDto, existingSpeaker);

        when(speakerRepository.save(existingSpeaker)).thenReturn(existingSpeaker);
        when(modelMapper.map(existingSpeaker, SpeakerOutDto.class)).thenReturn(speakerOutDto);

        SpeakerOutDto result = speakerService.modify(id, speakerInDto);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("AnaUpdated", result.getFirstName());

        // Importante: workshopHoursTotal se preserva (no viene del cliente)
        assertEquals(15, existingSpeaker.getWorkshopHoursTotal());

        verify(speakerRepository, times(1)).findById(id);
        verify(speakerRepository, times(1)).save(existingSpeaker);
    }

    @Test
    public void testModify_NotFound() {
        long id = 5L;

        SpeakerInDto speakerInDto = new SpeakerInDto("AnaUpdated", "LopezUpdated", "ana.updated@mail.com", "oratoria", 8, 999f, false, LocalDate.of(2025, 1, 1)
        );

        when(speakerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(SpeakerNotFoundException.class, () -> speakerService.modify(id, speakerInDto));

        verify(speakerRepository, times(1)).findById(id);
        verify(speakerRepository, times(0)).save(any(Speaker.class));
    }

    @Test
    public void testDelete() throws SpeakerNotFoundException {
        Speaker speaker = new Speaker(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5, 0, true, LocalDate.of(2025, 1, 1), null
        );

        when(speakerRepository.findById(1L)).thenReturn(Optional.of(speaker));

        speakerService.delete(1L);

        verify(speakerRepository, times(1)).findById(1L);
        verify(speakerRepository, times(1)).delete(speaker);
    }

    @Test
    public void testDelete_NotFound() {
        when(speakerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SpeakerNotFoundException.class, () -> speakerService.delete(99L));

        verify(speakerRepository, times(1)).findById(99L);
        verify(speakerRepository, times(0)).delete(any(Speaker.class));
    }
}
