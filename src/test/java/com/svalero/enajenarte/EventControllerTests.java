package com.svalero.enajenarte;

import com.fasterxml.jackson.core.type.TypeReference;
import com.svalero.enajenarte.controller.EventController;
import com.svalero.enajenarte.dto.EventInDto;
import com.svalero.enajenarte.dto.EventOutDto;
import com.svalero.enajenarte.exception.EventNotFoundException;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.service.EventService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
public class EventControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;


    @Test
    public void testGetAll() throws Exception {
        List<EventOutDto> eventsOutDtoList = List.of(
                new EventOutDto(1L, "Mindfulness", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 1L),
                new EventOutDto(2L, "Arte terapia", "Madrid", LocalDateTime.of(2026, 3, 1, 18, 0), 10, true, 1L)
        );

        when(eventService.findAll("", "", "")).thenReturn(eventsOutDtoList);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/events")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<EventOutDto> eventsListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(eventsListResponse);
        assertEquals(2, eventsListResponse.size());
        assertEquals("Mindfulness", eventsListResponse.getFirst().getTitle());
    }


    @Test
    public void testGetAllByTitle() throws Exception {
        List<EventOutDto> eventsOutDtoList = List.of(
                new EventOutDto(1L, "Mindfulness", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 1L),
                new EventOutDto(2L, "Mindfulness avanzado", "Zaragoza", LocalDateTime.of(2026, 2, 15, 10, 0), 0, true, 1L)
        );

        when(eventService.findAll("mind", "", "")).thenReturn(eventsOutDtoList);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/events")
                                .queryParam("title", "mind")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<EventOutDto> eventsListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(eventsListResponse);
        assertEquals(2, eventsListResponse.size());
        assertEquals("Mindfulness", eventsListResponse.getFirst().getTitle());
    }

    @Test
    public void testGetAllByLocation() throws Exception {
        List<EventOutDto> eventsOutDtoList = List.of(
                new EventOutDto(1L, "Mindfulness", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0),
                        0, true, 1L),
                new EventOutDto(3L, "Yoga", "Zaragoza", LocalDateTime.of(2026, 2, 20, 18, 0),
                        5, true, 1L)
        );

        when(eventService.findAll("", "zaragoza", "")).thenReturn(eventsOutDtoList);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/events")
                                .queryParam("location", "zaragoza")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<EventOutDto> eventsListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(eventsListResponse);
        assertEquals(2, eventsListResponse.size());
        assertEquals("Zaragoza", eventsListResponse.getFirst().getLocation());
    }

    @Test
    public void testGetAllByIsPublic() throws Exception {
        List<EventOutDto> eventsOutDtoList = List.of(
                new EventOutDto(1L, "Mindfulness", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0),
                        0, true, 1L),
                new EventOutDto(2L, "Arte terapia", "Madrid", LocalDateTime.of(2026, 3, 1, 18, 0),
                        10, true, 1L)
        );

        when(eventService.findAll("", "", "true")).thenReturn(eventsOutDtoList);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/events")
                                .queryParam("isPublic", "true")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<EventOutDto> eventsListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(eventsListResponse);
        assertEquals(2, eventsListResponse.size());
        assertTrue(eventsListResponse.getFirst().isPublic());
    }
    @Test
    public void testGetById() throws Exception {
        EventOutDto eventOutDto = new EventOutDto(7L, "Mindfulness", "Zaragoza",
                LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 1L);

        when(eventService.findById(7L)).thenReturn(eventOutDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/events/7")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    // GET 404
    @Test
    public void testGetById_NotFound() throws Exception {
        when(eventService.findById(99L)).thenThrow(new EventNotFoundException());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/events/99")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNotFound());
    }

    // POST 400
    @Test
    public void testAdd() throws Exception {
        EventInDto eventInDto = new EventInDto("Mindfulness", "Zaragoza",
                LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 30, 1L);

        EventOutDto eventOutDto = new EventOutDto(10L, "Mindfulness", "Zaragoza",
                LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 1L);

        when(eventService.add(any(EventInDto.class))).thenReturn(eventOutDto);

        String body = objectMapper.writeValueAsString(eventInDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/events")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(body))
                .andExpect(status().isCreated());
    }

    // POST 404
    @Test
    public void testAdd_SpeakerNotFound() throws Exception {
        EventInDto eventInDto = new EventInDto("Mindfulness", "Zaragoza",
                LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 30, 99L);

        when(eventService.add(any(EventInDto.class))).thenThrow(new SpeakerNotFoundException());

        String body = objectMapper.writeValueAsString(eventInDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/events")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    // POST 400
    @Test
    public void testAdd_BadRequest() throws Exception {
        // title vacío → @NotEmpty => 400
        EventInDto invalidEvent = new EventInDto("", "Zaragoza", LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 20, 1L);

        String body = objectMapper.writeValueAsString(invalidEvent);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/events")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isBadRequest());
    }

    // PUT
    @Test
    public void testModify() throws Exception {
        long id = 7L;

        EventInDto eventInDto = new EventInDto("Mindfulness actualizado", "Zaragoza",
                LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 30, 1L);

        EventOutDto outDto = new EventOutDto(id, "Mindfulness actualizado", "Zaragoza",
                LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 1L);

        when(eventService.modify(eq(id), any(EventInDto.class))).thenReturn(outDto);

        String body = objectMapper.writeValueAsString(eventInDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/events/7")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(body))
                .andExpect(status().isOk());
    }

    // PUT 404 - Not Found
    @Test
    public void testModify_NotFound() throws Exception {
        long id = 99L;

        EventInDto eventInDto = new EventInDto("Mindfulness", "Zaragoza",
                LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 30, 1L);

        when(eventService.modify(eq(id), any(EventInDto.class))).thenThrow(new EventNotFoundException());

        String body = objectMapper.writeValueAsString(eventInDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/events/99")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    // PUT 400
    @Test
    public void testModify_BadRequest() throws Exception {
        long id = 7L;

        // title vacío -> @NotEmpty -> 400
        EventInDto invalidDto = new EventInDto("", "Zaragoza",
                LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 30, 1L);

        String body = objectMapper.writeValueAsString(invalidDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/events/7")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // PUT 404
    @Test
    public void testModify_SpeakerNotFound() throws Exception {
        long id = 7L;

        EventInDto eventInDto = new EventInDto("Mindfulness", "Zaragoza",
                LocalDateTime.of(2026, 2, 1, 10, 0), 0, true, 30, 99L);

        when(eventService.modify(eq(id), any(EventInDto.class))).thenThrow(new SpeakerNotFoundException());

        String body = objectMapper.writeValueAsString(eventInDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/events/7")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDelete() throws Exception {
        doNothing().when(eventService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/events/1"))
                .andExpect(status().isNoContent());
    }

    // DELETE 404
    @Test
    public void testDelete_NotFound() throws Exception {
        doThrow(new EventNotFoundException()).when(eventService).delete(99L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/events/99"))
                .andExpect(status().isNotFound());
    }

}
