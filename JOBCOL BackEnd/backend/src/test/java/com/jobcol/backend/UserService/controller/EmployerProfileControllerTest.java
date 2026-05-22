package com.jobcol.backend.UserService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcol.backend.UserService.service.EmployerProfileService;
import com.jobcol.backend.shared.dto.EmployerProfileDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployerProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployerProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployerProfileService employerProfileService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployerProfileDTO employerProfileDTO;

    @BeforeEach
    void setUp() {
        employerProfileDTO = EmployerProfileDTO.builder()
                .companyName("TechCorp")
                .description("Empresa de tecnología")
                .location("Medellín")
                .averageRating(0.0)
                .totalJobsPosted(0)
                .build();
    }

    @Test
    void createProfile_shouldReturn201() throws Exception {
        when(employerProfileService.createProfile(any(EmployerProfileDTO.class)))
                .thenReturn(employerProfileDTO);

        mockMvc.perform(post("/employer-profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employerProfileDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName").value("TechCorp"));
    }

    @Test
    void getProfile_whenExists_shouldReturn200() throws Exception {
        when(employerProfileService.getByUserId(1L)).thenReturn(employerProfileDTO);

        mockMvc.perform(get("/employer-profiles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Medellín"));
    }

    @Test
    void getProfile_whenNotFound_shouldReturn404() throws Exception {
        when(employerProfileService.getByUserId(99L))
                .thenThrow(new EntityNotFoundException("Perfil no encontrado"));

        mockMvc.perform(get("/employer-profiles/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProfile_shouldReturn200() throws Exception {
        when(employerProfileService.updateProfile(eq(1L), any(EmployerProfileDTO.class)))
                .thenReturn(employerProfileDTO);

        mockMvc.perform(put("/employer-profiles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employerProfileDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("TechCorp"));
    }

    @Test
    void updateProfile_whenNotFound_shouldReturn404() throws Exception {
        when(employerProfileService.updateProfile(eq(99L), any(EmployerProfileDTO.class)))
                .thenThrow(new EntityNotFoundException("Perfil no encontrado"));

        mockMvc.perform(put("/employer-profiles/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employerProfileDTO)))
                .andExpect(status().isNotFound());
    }
}