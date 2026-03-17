package com.svalero.enajenarte;

import com.fasterxml.jackson.core.type.TypeReference;
import com.svalero.enajenarte.controller.SpeakerController;
import com.svalero.enajenarte.dto.SpeakerInDto;
import com.svalero.enajenarte.dto.SpeakerOutDto;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.service.SpeakerService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpeakerController.class)
public class SpeakerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpeakerService speakerService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    public void testGetAll() throws Exception {
        List<SpeakerOutDto> speakersOutDtoList = List.of(
                new SpeakerOutDto(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5),
                new SpeakerOutDto(2L, "Carlos", "Perez", "carlos@mail.com", "oratoria", 8)
        );

        when(speakerService.findAll("", "", "")).thenReturn(speakersOutDtoList);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/speakers")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<SpeakerOutDto> speakersListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(speakersListResponse);
        assertEquals(2, speakersListResponse.size());
        assertEquals("Ana", speakersListResponse.getFirst().getFirstName());
    }

    @Test
    public void testGetAllBySpeciality() throws Exception {
        List<SpeakerOutDto> speakersOutDtoList = List.of(
                new SpeakerOutDto(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5),
                new SpeakerOutDto(3L, "Lucia", "Martin", "lucia@mail.com", "mindfulness", 3)
        );

        when(speakerService.findAll("mind", "", "")).thenReturn(speakersOutDtoList);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/speakers")
                                .queryParam("speciality", "mind")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<SpeakerOutDto> speakersListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(speakersListResponse);
        assertEquals(2, speakersListResponse.size());
        assertEquals("Ana", speakersListResponse.getFirst().getFirstName());
    }

    @Test
    public void testGetAllByAvailable() throws Exception {
        List<SpeakerOutDto> speakersOutDtoList = List.of(
                new SpeakerOutDto(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5),
                new SpeakerOutDto(3L, "Lucia", "Martin", "lucia@mail.com", "oratoria", 3)
        );

        when(speakerService.findAll("", "true", "")).thenReturn(speakersOutDtoList);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/speakers")
                                .queryParam("available", "true")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<SpeakerOutDto> speakersListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(speakersListResponse);
        assertEquals(2, speakersListResponse.size());
        assertEquals("Ana", speakersListResponse.getFirst().getFirstName());
    }

    @Test
    public void testGetAllByYearsExperience() throws Exception {
        List<SpeakerOutDto> speakersOutDto = List.of(
                new SpeakerOutDto(1L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5),
                new SpeakerOutDto(2L, "Pedro", "Sanchez", "pedro@mail.com", "coaching", 5)
        );

        when(speakerService.findAll("", "", "5")).thenReturn(speakersOutDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/speakers")
                                .queryParam("yearsExperience", "5")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<SpeakerOutDto> speakersListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(speakersListResponse);
        assertEquals(2, speakersListResponse.size());
        assertEquals(5, speakersListResponse.getFirst().getYearsExperience());
    }

    @Test
    public void testGetById() throws Exception {
        SpeakerOutDto speakerOutDto = new SpeakerOutDto(7L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5);

        when(speakerService.findById(7L)).thenReturn(speakerOutDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/speakers/7")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk());
    }

    @Test // 404
    public void testGetById_NotFound() throws Exception {
        when(speakerService.findById(99L)).thenThrow(new SpeakerNotFoundException());

        mockMvc.perform(

                        MockMvcRequestBuilders.get("/speakers/99")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNotFound());
    }

    // POST
    @Test
    public void testAdd() throws Exception {
        SpeakerInDto speakerinDto = new SpeakerInDto("Ana", "Lopez", "ana@mail.com", "mindfulness", 5, 10f, true, LocalDate.of(2025, 1, 1)
        );

        SpeakerOutDto speakerOutDto = new SpeakerOutDto(10L, "Ana", "Lopez", "ana@mail.com", "mindfulness", 5);

        when(speakerService.add(any(SpeakerInDto.class))).thenReturn(speakerOutDto);

        String body = objectMapper.writeValueAsString(speakerinDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/speakers")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isCreated());
    }
    @Test // 400 firstName vacío
    public void testAdd_BadRequest() throws Exception {
        // firstName vacío
        SpeakerInDto invalidSpeaker = new SpeakerInDto("", "Lopez", "ana@mail.com", "mindfulness", 5, 10f, true, LocalDate.of(2025, 1, 1)
        );

        String body = objectMapper.writeValueAsString(invalidSpeaker);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/speakers")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isBadRequest());
    }

    // PUT
    @Test
    public void testModify() throws Exception {
        SpeakerInDto speakerInDto = new SpeakerInDto("AnaUpdated", "LopezUpdated", "ana.updated@mail.com", "oratoria", 8, 999f, false, LocalDate.of(2025, 1, 1)
        );

        SpeakerOutDto speakeroutDto = new SpeakerOutDto(5L, "AnaUpdated", "LopezUpdated", "ana.updated@mail.com", "oratoria", 8);

        when(speakerService.modify(eq(5L), any(SpeakerInDto.class))).thenReturn(speakeroutDto);

        String body = objectMapper.writeValueAsString(speakerInDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/speakers/5")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isOk());
    }

    // PUT 404
    @Test
    public void testModify_NotFound() throws Exception {
        SpeakerInDto speakerinDto = new SpeakerInDto("AnaUpdated", "LopezUpdated", "ana.updated@mail.com", "oratoria", 8, 999f, false, LocalDate.of(2025, 1, 1)
        );

        when(speakerService.modify(eq(99L), any(SpeakerInDto.class))).thenThrow(new SpeakerNotFoundException());

        String body = objectMapper.writeValueAsString(speakerinDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/speakers/99")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDelete() throws Exception {
        doNothing().when(speakerService).delete(1L);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/speakers/1"))
                .andExpect(status().isNoContent());
    }

    // DELETE 404
    @Test
    public void testDelete_NotFound() throws Exception {
        doThrow(new SpeakerNotFoundException()).when(speakerService).delete(99L);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/speakers/99"))
                .andExpect(status().isNotFound());
    }

}
