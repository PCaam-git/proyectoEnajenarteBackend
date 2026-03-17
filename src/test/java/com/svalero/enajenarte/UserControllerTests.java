package com.svalero.enajenarte;

import com.fasterxml.jackson.core.type.TypeReference;
import com.svalero.enajenarte.controller.UserController;
import com.svalero.enajenarte.dto.UserInDto;
import com.svalero.enajenarte.dto.UserOutDto;
import com.svalero.enajenarte.exception.UserNotFoundException;
import com.svalero.enajenarte.service.UserService;
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

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ModelMapper modelMapper;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    // 20X
    @Test
    public void testGetAll() throws Exception {
        List<UserOutDto> usersOutDto = List.of(
                new UserOutDto(1L, "patricia", "patricia@mail.com", "Patricia User", "user"),
                new UserOutDto(2L, "mario", "mario@mail.com", "Mario User", "user")
        );

        when(userService.findAll("", "", "")).thenReturn(usersOutDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/users")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<UserOutDto> usersListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(usersListResponse);
        assertEquals(2, usersListResponse.size());
        assertEquals("patricia", usersListResponse.getFirst().getUsername());
    }

    // 20X + FILTRO
    @Test
    public void testGetAllByUsername() throws Exception {
        List<UserOutDto> usersOutDto = List.of(
                new UserOutDto(1L, "patricia", "patricia@mail.com", "Patricia User", "user"),
                new UserOutDto(3L, "patricia.dev", "patricia.dev@mail.com", "Patricia Dev", "user")
        );

        when(userService.findAll("patricia", "", "")).thenReturn(usersOutDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/users")
                                .queryParam("username", "patricia")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<UserOutDto> usersListResponse =
                objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(usersListResponse);
        assertEquals(2, usersListResponse.size());
        assertEquals("patricia", usersListResponse.getFirst().getUsername());
    }

    @Test
    public void testGetById() throws Exception {
        UserOutDto userOutDto = new UserOutDto(7L, "patricia", "patricia@mail.com", "Patricia User", "user");

        when(userService.findById(7L)).thenReturn(userOutDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/7")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk());
    }

    // 404
    @Test
    public void testGetById_NotFound() throws Exception {
        when(userService.findById(99L)).thenThrow(new UserNotFoundException());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/99")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAdd() throws Exception {
        UserInDto userInDto = new UserInDto("patricia", "password", "patricia@mail.com", "Patricia User", 25);

        UserOutDto createdUserOutDto = new UserOutDto(10L, "patricia", "patricia@mail.com", "Patricia User", "user");
        when(userService.add(any(UserInDto.class))).thenReturn(createdUserOutDto);

        String requestBody = objectMapper.writeValueAsString(userInDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());
    }

    //  400
    // username vacío
    @Test
    public void testAdd_BadRequest() throws Exception {

        UserInDto invalidUser = new UserInDto("", "password", "user@mail.com", "Invalid User", 25);

        String body = objectMapper.writeValueAsString(invalidUser);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(body)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testModify() throws Exception {
        UserInDto userInDto = new UserInDto("updatedUsername", "password", "updated@mail.com", "Updated Full Name", 30);

        UserOutDto updatedUserOutDto = new UserOutDto(5L, "updatedUsername", "updated@mail.com", "Updated Full Name", "user");
        when(userService.modify(eq(5L), any(UserInDto.class))).thenReturn(updatedUserOutDto);

        String requestBody = objectMapper.writeValueAsString(userInDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/users/5")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(requestBody)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void testModify_NotFound() throws Exception {
        UserInDto userInDto = new UserInDto("updatedUsername", "password", "updated@mail.com", "Updated Full Name", 30);

        when(userService.modify(eq(99L), any(UserInDto.class))).thenThrow(new UserNotFoundException());

        String requestBody = objectMapper.writeValueAsString(userInDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/users/99")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void testModify_BadRequest() throws Exception {
        // username vacío -> @NotEmpty => 400
        UserInDto invalidUserInDto = new UserInDto("", "password", "updated@mail.com", "Updated Full Name", 30);

        String requestBody = objectMapper.writeValueAsString(invalidUserInDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/users/5")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDelete() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/users/1")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDelete_NotFound() throws Exception {
        doThrow(new UserNotFoundException()).when(userService).delete(99L);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/users/99")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isNotFound());
    }
}
