package com.svalero.enajenarte;

import com.fasterxml.jackson.core.type.TypeReference;
import com.svalero.enajenarte.controller.RegistrationController;
import com.svalero.enajenarte.dto.RegistrationInDto;
import com.svalero.enajenarte.dto.RegistrationOutDto;
import com.svalero.enajenarte.exception.RegistrationNotFoundException;
import com.svalero.enajenarte.exception.UserNotFoundException;
import com.svalero.enajenarte.exception.WorkshopNotFoundException;
import com.svalero.enajenarte.service.RegistrationService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrationController.class)
public class RegistrationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    public void testGetAll() throws Exception {
        List<RegistrationOutDto> registrationOutDtoList = List.of(
                new RegistrationOutDto(1L, LocalDate.of(2026, 1, 10), "CONF-1", false, 2, 0, 0, 1L, 10L),
                new RegistrationOutDto(2L, LocalDate.of(2026, 1, 11),"CONF-2", true, 1, 20, 5, 2L, 10L)
        );

        when(registrationService.findAll("", "", "")).thenReturn(registrationOutDtoList);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/registrations")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<RegistrationOutDto> responseList =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(responseList);
        assertEquals(2, responseList.size());
        assertEquals(2, responseList.getFirst().getNumberOfTickets());
    }

    @Test
    public void testGetAllByUserId() throws Exception {
        List<RegistrationOutDto> registrationOutDtoList = List.of(
                new RegistrationOutDto(1L, LocalDate.of(2026, 1, 10), "CONF-1",false, 2, 0, 0, 1L, 10L),
                new RegistrationOutDto(3L, LocalDate.of(2026, 1, 12),"CONF-3", true, 1, 20, 4, 1L, 11L)
        );

        when(registrationService.findAll("1", "", "")).thenReturn(registrationOutDtoList);

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/registrations")
                                .queryParam("userId", "1")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<RegistrationOutDto> responseList =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(responseList);
        assertEquals(2, responseList.size());
        assertEquals(1L, responseList.getFirst().getUserId());
    }

    @Test
    public void testGetAllByWorkshopId() throws Exception {
        List<RegistrationOutDto> registrationOutDtoList = List.of(
                new RegistrationOutDto(1L, LocalDate.of(2026, 1, 10), "CONF-1", false, 2, 0, 0, 1L, 10L),
                new RegistrationOutDto(2L, LocalDate.of(2026, 1, 11), "CONF-3", true, 1, 20, 5, 2L, 10L)
        );

        when(registrationService.findAll("10", "", "")).thenReturn(registrationOutDtoList);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/registrations")
                                .queryParam("workshopId", "10")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllByIsPaid() throws Exception {
        List<RegistrationOutDto> registrationOutDtoList = List.of(
                new RegistrationOutDto(2L, LocalDate.of(2026, 1, 11), "CONF-3", true, 1, 20, 5, 2L, 10L)
        );

        when(registrationService.findAll("", "", "true")).thenReturn(registrationOutDtoList);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/registrations")
                                .queryParam("isPaid", "true")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void testGetById() throws Exception {
        RegistrationOutDto registrationOutDto =
                new RegistrationOutDto(7L, LocalDate.of(2026, 1, 10),"CONF-7",  false, 2, 0, 0, 1L, 10L);

        when(registrationService.findById(7L)).thenReturn(registrationOutDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/registrations/7")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void testGetById_NotFound() throws Exception {
        when(registrationService.findById(99L)).thenThrow(new RegistrationNotFoundException());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/registrations/99")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAdd() throws Exception {
        RegistrationInDto registrationInDto = new RegistrationInDto(2, 1L, 10L);

        RegistrationOutDto registrationOutDto =
                new RegistrationOutDto(100L, LocalDate.of(2026, 1, 10),"CONF-10",  false, 2, 0, 0, 1L, 10L);

        when(registrationService.add(any(RegistrationInDto.class))).thenReturn(registrationOutDto);

        String body = objectMapper.writeValueAsString(registrationInDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/registrations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isCreated());
    }

    @Test
    public void testAdd_BadRequest() throws Exception {
        RegistrationInDto invalidRegistrationInDto = new RegistrationInDto(0, 1L, 10L);
        String body = objectMapper.writeValueAsString(invalidRegistrationInDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/registrations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAdd_UserNotFound() throws Exception {
        RegistrationInDto registrationInDto = new RegistrationInDto(2, 99L, 10L);

        when(registrationService.add(any(RegistrationInDto.class))).thenThrow(new UserNotFoundException());

        String body = objectMapper.writeValueAsString(registrationInDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/registrations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAdd_WorkshopNotFound() throws Exception {
        RegistrationInDto registrationInDto = new RegistrationInDto(2, 1L, 99L);

        when(registrationService.add(any(RegistrationInDto.class))).thenThrow(new WorkshopNotFoundException());

        String body = objectMapper.writeValueAsString(registrationInDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/registrations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testModify() throws Exception {
        RegistrationInDto registrationInDto = new RegistrationInDto(3, 1L, 10L);

        RegistrationOutDto registrationOutDto =
                new RegistrationOutDto(5L, LocalDate.of(2026, 1, 10),"CONF-10", false, 3, 0, 0, 1L, 10L);

        when(registrationService.modify(5L, registrationInDto)).thenReturn(registrationOutDto);

        String body = objectMapper.writeValueAsString(registrationInDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/registrations/5")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void testModify_NotFound() throws Exception {
        RegistrationInDto registrationInDto = new RegistrationInDto(3, 1L, 10L);

        when(registrationService.modify(99L, registrationInDto)).thenThrow(new RegistrationNotFoundException());

        String body = objectMapper.writeValueAsString(registrationInDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/registrations/99")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testModify_UserNotFound() throws Exception {
        RegistrationInDto registrationInDto = new RegistrationInDto(3, 99L, 10L);

        when(registrationService.modify(5L, registrationInDto)).thenThrow(new UserNotFoundException());

        String body = objectMapper.writeValueAsString(registrationInDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/registrations/5")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testModify_WorkshopNotFound() throws Exception {
        RegistrationInDto registrationInDto = new RegistrationInDto(3, 1L, 99L);

        when(registrationService.modify(5L, registrationInDto)).thenThrow(new WorkshopNotFoundException());

        String body = objectMapper.writeValueAsString(registrationInDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/registrations/5")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDelete() throws Exception {
        doNothing().when(registrationService).delete(1L);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/registrations/1")
                )
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDelete_NotFound() throws Exception {
        doThrow(new RegistrationNotFoundException()).when(registrationService).delete(99L);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/registrations/99")
                )
                .andExpect(status().isNotFound());
    }
}
