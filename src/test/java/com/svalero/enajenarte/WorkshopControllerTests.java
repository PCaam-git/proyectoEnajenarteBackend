package com.svalero.enajenarte;

import com.fasterxml.jackson.core.type.TypeReference;
import com.svalero.enajenarte.controller.WorkshopController;
import com.svalero.enajenarte.dto.WorkshopInDto;
import com.svalero.enajenarte.dto.WorkshopOutDto;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.exception.WorkshopNotFoundException;
import com.svalero.enajenarte.service.WorkshopService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkshopController.class)
public class WorkshopControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkshopService workshopService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    // 20x
    @Test
    public void testGetAll() throws Exception {
        List<WorkshopOutDto> workshopsOutDtoList = List.of(
                new WorkshopOutDto(1L, "Oratoria básica", "Taller de oratoria y comunicación", LocalDate.of(2026, 2, 22), 90, 25, true, 1L),
                new WorkshopOutDto(2L, "Arte terapia", "Taller creativo para autocuidado", LocalDate.of(2026, 3, 5), 120, 30, false, 1L)
        );

        when(workshopService.findAll("", "", "")).thenReturn(workshopsOutDtoList);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/workshops")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<WorkshopOutDto> workshopsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(workshopsListResponse);
        assertEquals(2, workshopsListResponse.size());
        assertEquals("Oratoria básica", workshopsListResponse.getFirst().getName());
    }

    // 20x con filtros
    @Test
    public void testGetAllByName() throws Exception {
        List<WorkshopOutDto> workshopsOutDtoList = List.of(
                new WorkshopOutDto(2L, "Arte terapia", "Taller creativo para autocuidado", LocalDate.of(2026, 3, 5), 120, 30, false, 1L),
                new WorkshopOutDto(3L, "Arte terapia avanzada", "Taller creativo avanzado", LocalDate.of(2026, 3, 20), 120, 35, false, 1L)
        );

        when(workshopService.findAll("arte", "", "")).thenReturn(workshopsOutDtoList);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/workshops")
                                .queryParam("name", "arte")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<WorkshopOutDto> workshopsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(workshopsListResponse);
        assertEquals(2, workshopsListResponse.size());
        assertEquals("Arte terapia", workshopsListResponse.getFirst().getName());
    }

    @Test
    public void testGetAllByIsOnline() throws Exception {
        List<WorkshopOutDto> workshopsOutDtoList = List.of(
                new WorkshopOutDto(1L, "Oratoria básica", "Taller de oratoria",
                        LocalDate.of(2026, 2, 10), 90, 25, true, 1L),
                new WorkshopOutDto(3L, "Coaching online", "Taller de coaching",
                        LocalDate.of(2026, 3, 15), 120, 30, true, 1L)
        );

        when(workshopService.findAll("", "true", "")).thenReturn(workshopsOutDtoList);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/workshops")
                                .queryParam("isOnline", "true")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<WorkshopOutDto> workshopsListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(workshopsListResponse);
        assertEquals(2, workshopsListResponse.size());
        assertTrue(workshopsListResponse.getFirst().isOnline());
    }

    @Test
    public void testGetAllBySpeakerId() throws Exception {
        List<WorkshopOutDto> workshopsOutDtoList = List.of(
                new WorkshopOutDto(1L, "Oratoria básica", "Taller de oratoria",
                        LocalDate.of(2026, 2, 10), 90, 25, true, 5L),
                new WorkshopOutDto(2L, "Oratoria avanzada", "Taller avanzado",
                        LocalDate.of(2026, 3, 5), 120, 30, false, 5L)
        );

        when(workshopService.findAll("", "", "5")).thenReturn(workshopsOutDtoList);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/workshops")
                                .queryParam("speakerId", "5")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<WorkshopOutDto> workshopsListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(workshopsListResponse);
        assertEquals(2, workshopsListResponse.size());
        assertEquals(5L, workshopsListResponse.getFirst().getSpeakerId());
    }

    @Test
    public void testGetAllBySpeakerId_SpeakerNotFound() throws Exception {
        when(workshopService.findAll("", "", "99")).thenThrow(new SpeakerNotFoundException());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/workshops")
                                .queryParam("speakerId", "99")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetById() throws Exception {
        WorkshopOutDto workshopOutDto = new WorkshopOutDto(7L, "Oratoria", "Taller de desarrollo", LocalDate.of(2026, 2, 10), 90, 25, true, 1L
        );

        when(workshopService.findById(7L)).thenReturn(workshopOutDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/workshops/7")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    // 404
    @Test
    public void testGetById_NotFound() throws Exception {
        when(workshopService.findById(99L)).thenThrow(new WorkshopNotFoundException());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/workshops/99")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAdd() throws Exception {
        WorkshopInDto workshopInDto = new WorkshopInDto("Oratoria", "Taller de desarrollo", LocalDate.of(2026, 5, 10), 90, 25, 20, true, 1L
        );

        WorkshopOutDto workshopOutDto = new WorkshopOutDto(10L, "Oratoria", "Taller de desarrollo", LocalDate.of(2026, 5, 10), 90, 25, true, 1L
        );

        when(workshopService.add(any(WorkshopInDto.class))).thenReturn(workshopOutDto);

        String body = objectMapper.writeValueAsString(workshopInDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/workshops")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(body))
                .andExpect(status().isCreated());
    }

    // 400
    // Name vacío
    @Test
    public void testAdd_BadRequest() throws Exception {
        WorkshopInDto invalidWorkshop = new WorkshopInDto("", "Taller de prueba", LocalDate.of(2026, 5, 10), 90, 25, 20, true, 1L
        );

        String body = objectMapper.writeValueAsString(invalidWorkshop);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/workshops")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testModify() throws Exception {
        WorkshopInDto workshopInDto = new WorkshopInDto("Oratoria actualizada", "Descripción actualizada", LocalDate.of(2026, 3, 10), 120, 30, 15, false, 1L
        );

        WorkshopOutDto workshopOutDto = new WorkshopOutDto(5L, "Oratoria actualizada", "Descripción actualizada", LocalDate.of(2026, 3, 10), 120, 30, false, 1L
        );

        when(workshopService.modify(eq(5L), any(WorkshopInDto.class))).thenReturn(workshopOutDto);

        String body = objectMapper.writeValueAsString(workshopInDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/workshops/5")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    public void testModify_NotFound() throws Exception {
        WorkshopInDto workshopInDto = new WorkshopInDto("Oratoria actualizada", "Descripción actualizada", LocalDate.of(2026, 3, 10), 120, 30, 15, false, 1L
        );

        doThrow(new WorkshopNotFoundException()).when(workshopService).modify(eq(99L), any(WorkshopInDto.class));

        String body = objectMapper.writeValueAsString(workshopInDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/workshops/99")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(body))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testDelete() throws Exception {
        doNothing().when(workshopService).delete(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/workshops/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDelete_NotFound() throws Exception {
        doThrow(new WorkshopNotFoundException()).when(workshopService).delete(99L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/workshops/99"))
                .andExpect(status().isNotFound());
    }
}
