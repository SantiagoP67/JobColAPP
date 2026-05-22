package com.jobcol.backend.UserService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcol.backend.UserService.service.UserService;
import com.jobcol.backend.shared.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("santiago@test.com");
        userDTO.setUsername("santi");
        userDTO.setFirstName("Santiago");
        userDTO.setLastName("Gomez");
        userDTO.setCedula("123456789");
        userDTO.setRole("TRABAJADOR");
    }

    // ── GET /users ──────────────────────────────────────────────

    @Test
    void getAllUsers_shouldReturn200WithList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userDTO));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("santiago@test.com"));
    }

    // ── GET /users/{id} ─────────────────────────────────────────

    @Test
    void getUserById_whenExists_shouldReturn200() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(userDTO));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("santi"));
    }

    @Test
    void getUserById_whenNotExists_shouldReturn404() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    // ── GET /users/email ────────────────────────────────────────

    @Test
    void getUserByEmail_whenExists_shouldReturn200() throws Exception {
        when(userService.getUserByEmail("santiago@test.com")).thenReturn(Optional.of(userDTO));

        mockMvc.perform(get("/users/email").param("email", "santiago@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("santiago@test.com"));
    }

    @Test
    void getUserByEmail_whenNotExists_shouldReturn404() throws Exception {
        when(userService.getUserByEmail("no@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/email").param("email", "no@test.com"))
                .andExpect(status().isNotFound());
    }

    // ── GET /users/keycloak/{id} ────────────────────────────────

    @Test
    void getUserByKeycloakId_whenExists_shouldReturn200() throws Exception {
        when(userService.getUserByKeycloakUserId("kc-abc123")).thenReturn(Optional.of(userDTO));

        mockMvc.perform(get("/users/keycloak/kc-abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserByKeycloakId_whenNotExists_shouldReturn404() throws Exception {
        when(userService.getUserByKeycloakUserId("kc-xxx")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/keycloak/kc-xxx"))
                .andExpect(status().isNotFound());
    }

    // ── PUT /users/{id} ─────────────────────────────────────────

    @Test
    void updateUser_shouldReturn200WithUpdatedDTO() throws Exception {
        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("santi"));
    }

    // ── DELETE /users/{id} ──────────────────────────────────────

    @Test
    void deleteUser_shouldReturn204() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    // ── PATCH /users/{id}/deactivate ────────────────────────────

    @Test
    void deactivateUser_shouldReturn204() throws Exception {
        doNothing().when(userService).deactivateUser(1L);

        mockMvc.perform(patch("/users/1/deactivate"))
                .andExpect(status().isNoContent());
    }

    // ── PUT /users/{id}/photo ───────────────────────────────────

    @Test
    void updatePhoto_shouldReturn200WithDTO() throws Exception {
        when(userService.updatePhoto(eq(1L), any())).thenReturn(userDTO);

        MockMultipartFile file = new MockMultipartFile(
                "file", "foto.jpg", "image/jpeg", "data".getBytes()
        );

        mockMvc.perform(multipart("/users/1/photo")
                        .file(file)
                        .with(req -> { req.setMethod("PUT"); return req; }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}