package com.jobcol.backend.UserService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcol.backend.UserService.service.ProfileService;
import com.jobcol.backend.shared.dto.ProfileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ProfileService profileService;
    @Autowired private ObjectMapper objectMapper;

    private ProfileDTO profileDTO;

    @BeforeEach
    void setUp() {
        profileDTO = ProfileDTO.builder()
                .id(1L)
                .skills("Java, Spring")
                .experience("2 años")
                .location("Bogotá")
                .visible(true)
                .userId(1L)
                .build();
    }

    // ── GET /profiles ────────────────────────────────────────────

    @Test
    void getAllProfiles_shouldReturn200WithList() throws Exception {
        when(profileService.getAllProfiles()).thenReturn(List.of(profileDTO));

        mockMvc.perform(get("/profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].skills").value("Java, Spring"));
    }

    // ── GET /profiles/{id} ───────────────────────────────────────

    @Test
    void getProfileById_whenExists_shouldReturn200() throws Exception {
        when(profileService.getProfileById(1L)).thenReturn(Optional.of(profileDTO));

        mockMvc.perform(get("/profiles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Bogotá"));
    }

    @Test
    void getProfileById_whenNotExists_shouldReturn404() throws Exception {
        when(profileService.getProfileById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/profiles/99"))
                .andExpect(status().isNotFound());
    }

    // ── GET /profiles/user/{userId} ──────────────────────────────

    @Test
    void getProfileByUserId_whenExists_shouldReturn200() throws Exception {
        when(profileService.getProfileByUserId(1L)).thenReturn(Optional.of(profileDTO));

        mockMvc.perform(get("/profiles/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void getProfileByUserId_whenNotExists_shouldReturn404() throws Exception {
        when(profileService.getProfileByUserId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/profiles/user/99"))
                .andExpect(status().isNotFound());
    }

    // ── POST /profiles ───────────────────────────────────────────

    @Test
    void createProfile_shouldReturn201WithDTO() throws Exception {
        when(profileService.createProfile(any(ProfileDTO.class))).thenReturn(profileDTO);

        mockMvc.perform(post("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    // ── PUT /profiles/{id} ───────────────────────────────────────

    @Test
    void updateProfile_shouldReturn200WithUpdatedDTO() throws Exception {
        when(profileService.updateProfile(eq(1L), any(ProfileDTO.class))).thenReturn(profileDTO);

        mockMvc.perform(put("/profiles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skills").value("Java, Spring"));
    }

    // ── DELETE /profiles/{id} ────────────────────────────────────

    @Test
    void deleteProfile_shouldReturn204() throws Exception {
        doNothing().when(profileService).deleteProfile(1L);

        mockMvc.perform(delete("/profiles/1"))
                .andExpect(status().isNoContent());
    }
}