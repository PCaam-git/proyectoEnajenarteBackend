package com.svalero.enajenarte;

import com.svalero.enajenarte.domain.Event;
import com.svalero.enajenarte.domain.Speaker;
import com.svalero.enajenarte.dto.EventInDto;
import com.svalero.enajenarte.dto.EventOutDto;
import com.svalero.enajenarte.exception.EventNotFoundException;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.repository.EventRepository;
import com.svalero.enajenarte.repository.SpeakerRepository;
import com.svalero.enajenarte.service.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTests {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SpeakerRepository speakerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() {
        List<Event> mockEventList = List.of(
                new Event(1L, "Mindfulness", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 30, null),
                new Event(2L, "Arte terapia", "Madrid", LocalDateTime.of(2026, 3, 1, 18, 0), 10, true, 50, null)
        );

        List<EventOutDto> modelMapperEventOutDto = List.of(
                new EventOutDto(1L, "Mindfulness", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 1L),
                new EventOutDto(2L, "Arte terapia", "Madrid", LocalDateTime.of(2026, 3, 1, 18, 0), 10, true, 1L)
        );

        when(eventRepository.findAll()).thenReturn(mockEventList);
        when(modelMapper.map(mockEventList, new TypeToken<List<EventOutDto>>() {}.getType())).thenReturn(modelMapperEventOutDto);

        List<EventOutDto> actualEventList = eventService.findAll("", "", "");

        assertEquals(2, actualEventList.size());
        assertEquals("Mindfulness", actualEventList.getFirst().getTitle());
        assertEquals("Arte terapia", actualEventList.getLast().getTitle());

        verify(eventRepository, times(1)).findAll();
        verify(eventRepository, times(0)).findByTitleContainingIgnoreCase(anyString());
        verify(eventRepository, times(0)).findByLocationContainingIgnoreCase(anyString());
        verify(eventRepository, times(0)).findByIsPublic(anyBoolean());
    }

    @Test
    public void testFindAllByTitle() {
        List<Event> mockEventList = List.of(
                new Event(1L, "Mindfulness", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 30, null),
                new Event(2L, "Mindfulness avanzado", "Zaragoza", LocalDateTime.of(2026, 2, 15, 10, 0), 0, true, 20, null)
        );

        List<EventOutDto> modelMapperEventOutDto = List.of(
                new EventOutDto(1L, "Mindfulness", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 1L),
                new EventOutDto(2L, "Mindfulness avanzado", "Zaragoza", LocalDateTime.of(2026, 2, 15, 10, 0), 0, true, 1L)
        );

        when(eventRepository.findByTitleContainingIgnoreCase("mind")).thenReturn(mockEventList);
        when(modelMapper.map(mockEventList, new TypeToken<List<EventOutDto>>() {}.getType())).thenReturn(modelMapperEventOutDto);

        List<EventOutDto> actualEventList = eventService.findAll("mind", "", "");

        assertEquals(2, actualEventList.size());
        assertEquals("Mindfulness", actualEventList.getFirst().getTitle());
        assertEquals("Mindfulness avanzado", actualEventList.getLast().getTitle());

        verify(eventRepository, times(0)).findAll();
        verify(eventRepository, times(1)).findByTitleContainingIgnoreCase("mind");
    }

    @Test
    public void testFindAllByLocation() {
        List<Event> mockEventList = List.of(
                new Event(1L, "Mindfulness", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0),
                        0, true, 30, null),
                new Event(3L, "Yoga", "Zaragoza", LocalDateTime.of(2026, 2, 20, 18, 0),
                        5, true, 25, null)
        );

        List<EventOutDto> modelMapperEventOutDto = List.of(
                new EventOutDto(1L, "Mindfulness", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0),
                        0, true, 1L),
                new EventOutDto(3L, "Yoga", "Zaragoza", LocalDateTime.of(2026, 2, 20, 18, 0),
                        5, true, 1L)
        );

        when(eventRepository.findByLocationContainingIgnoreCase("zaragoza")).thenReturn(mockEventList);
        when(modelMapper.map(mockEventList, new TypeToken<List<EventOutDto>>() {}.getType()))
                .thenReturn(modelMapperEventOutDto);

        List<EventOutDto> actualEventList = eventService.findAll("", "zaragoza", "");

        assertEquals(2, actualEventList.size());
        assertEquals("Mindfulness", actualEventList.getFirst().getTitle());
        assertEquals("Yoga", actualEventList.getLast().getTitle());

        verify(eventRepository, times(0)).findAll();
        verify(eventRepository, times(0)).findByTitleContainingIgnoreCase(anyString());
        verify(eventRepository, times(1)).findByLocationContainingIgnoreCase("zaragoza");
        verify(eventRepository, times(0)).findByIsPublic(anyBoolean());
    }

    @Test
    public void testFindAllByIsPublic() {
        List<Event> mockEventList = List.of(
                new Event(1L, "Mindfulness", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0),
                        0, true, 30, null),
                new Event(2L, "Arte terapia", "Madrid", LocalDateTime.of(2026, 3, 1, 18, 0),
                        10, true, 50, null)
        );

        List<EventOutDto> modelMapperEventOutDto = List.of(
                new EventOutDto(1L, "Mindfulness", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0),
                        0, true, 1L),
                new EventOutDto(2L, "Arte terapia", "Madrid", LocalDateTime.of(2026, 3, 1, 18, 0),
                        10, true, 1L)
        );

        when(eventRepository.findByIsPublic(true)).thenReturn(mockEventList);
        when(modelMapper.map(mockEventList, new TypeToken<List<EventOutDto>>() {}.getType()))
                .thenReturn(modelMapperEventOutDto);

        List<EventOutDto> actualEventList = eventService.findAll("", "", "true");

        assertEquals(2, actualEventList.size());
        assertEquals("Mindfulness", actualEventList.getFirst().getTitle());
        assertEquals("Arte terapia", actualEventList.getLast().getTitle());

        verify(eventRepository, times(0)).findAll();
        verify(eventRepository, times(0)).findByTitleContainingIgnoreCase(anyString());
        verify(eventRepository, times(0)).findByLocationContainingIgnoreCase(anyString());
        verify(eventRepository, times(1)).findByIsPublic(true);
    }

    @Test
    public void testFindById() throws EventNotFoundException {
        Event event = new Event(7L, "Mindfulness", "Zaragoza",
                LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 30, null);

        EventOutDto eventOutDto = new EventOutDto(7L, "Mindfulness", "Zaragoza",
                LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 1L);

        when(eventRepository.findById(7L)).thenReturn(Optional.of(event));
        when(modelMapper.map(event, EventOutDto.class)).thenReturn(eventOutDto);

        EventOutDto actualEventOutDto = eventService.findById(7L);

        assertEquals(7L, actualEventOutDto.getId());
        assertEquals("Mindfulness", actualEventOutDto.getTitle());
        verify(eventRepository, times(1)).findById(7L);
    }

    @Test
    public void testFindById_NotFound() {
        when(eventRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.findById(7L));
        verify(eventRepository, times(1)).findById(7L);
    }

    @Test
    public void testAdd() throws SpeakerNotFoundException {
        EventInDto eventInDto = new EventInDto("Mindfulness", "Zaragoza",
                LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 30, 1L);

        Speaker speaker = new Speaker();
        speaker.setId(1L);

        Event mappedEvent = new Event();
        Event savedEvent = new Event();
        savedEvent.setId(10L);

        EventOutDto outDto = new EventOutDto(10L, "Mindfulness", "Zaragoza",
                LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 1L);

        when(speakerRepository.findById(1L)).thenReturn(Optional.of(speaker));
        when(modelMapper.map(eventInDto, Event.class)).thenReturn(mappedEvent);
        when(eventRepository.save(mappedEvent)).thenReturn(savedEvent);
        when(modelMapper.map(savedEvent, EventOutDto.class)).thenReturn(outDto);

        EventOutDto actual = eventService.add(eventInDto);

        assertEquals(10L, actual.getId());
        verify(speakerRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(mappedEvent);
    }

    @Test
    public void testAdd_SpeakerNotFound() {
        EventInDto eventInDto = new EventInDto("Mindfulness", "Zaragoza",
                LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 30, 99L);

        when(speakerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SpeakerNotFoundException.class, () -> eventService.add(eventInDto));
        verify(speakerRepository, times(1)).findById(99L);
        verify(eventRepository, times(0)).save(any(Event.class));
    }

    @Test
    public void testDelete() throws EventNotFoundException {
        Event event = new Event();
        event.setId(1L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.delete(1L);

        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).delete(event);
    }

    @Test
    public void testDelete_NotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.delete(1L));
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(0)).delete(any(Event.class));
    }

    @Test
    public void testModify() throws EventNotFoundException, SpeakerNotFoundException {
        long id = 5L;

        Event existingEvent = new Event();
        existingEvent.setId(id);

        Speaker speaker = new Speaker();
        speaker.setId(1L);

        EventInDto eventInDto = new EventInDto("Updated title", "Updated location",
                LocalDateTime.of(2026, 3, 1, 10, 0), 10, false, 100, 1L);

        Event savedEvent = new Event();
        savedEvent.setId(id);

        EventOutDto outDto = new EventOutDto();
        outDto.setId(id);
        outDto.setTitle("Updated title");

        when(eventRepository.findById(id)).thenReturn(Optional.of(existingEvent));
        when(speakerRepository.findById(1L)).thenReturn(Optional.of(speaker));

        doNothing().when(modelMapper).map(eventInDto, existingEvent);

        when(eventRepository.save(existingEvent)).thenReturn(savedEvent);
        when(modelMapper.map(savedEvent, EventOutDto.class)).thenReturn(outDto);

        EventOutDto actual = eventService.modify(id, eventInDto);

        assertEquals(id, actual.getId());
        assertEquals("Updated title", actual.getTitle());

        verify(eventRepository, times(1)).findById(id);
        verify(speakerRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(existingEvent);
    }

    @Test
    public void testModify_EventNotFound() {
        long id = 5L;

        EventInDto eventInDto = new EventInDto("Updated title", "Updated location",
                LocalDateTime.of(2026, 3, 1, 10, 0), 10, false, 100, 1L);

        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.modify(id, eventInDto));
        verify(eventRepository, times(1)).findById(id);
        verify(eventRepository, times(0)).save(any(Event.class));
    }

    @Test
    public void testModify_SpeakerNotFound() {
        long id = 5L;

        Event existingEvent = new Event();
        existingEvent.setId(id);

        EventInDto eventInDto = new EventInDto("Updated title", "Updated location",
                LocalDateTime.of(2026, 3, 1, 10, 0), 10, false, 100, 99L);

        when(eventRepository.findById(id)).thenReturn(Optional.of(existingEvent));
        when(speakerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SpeakerNotFoundException.class, () -> eventService.modify(id, eventInDto));
        verify(eventRepository, times(1)).findById(id);
        verify(speakerRepository, times(1)).findById(99L);
        verify(eventRepository, times(0)).save(any(Event.class));
    }
}
