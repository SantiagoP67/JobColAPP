package com.jobcol.backend.PostulationService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcol.backend.PostulationService.service.PostulationService;
import com.jobcol.backend.shared.dto.OfferDTO;
import com.jobcol.backend.shared.dto.PostulationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostulationController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostulationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostulationService postulationService;

    @Autowired
    private ObjectMapper objectMapper;

    private PostulationDTO postulationDTO;

    @BeforeEach
    void setUp() {
        OfferDTO offerDTO = new OfferDTO(
                1L, "Desarrollador Java", "desc", "TI",
                "Bogotá", 5000000, "OPEN", LocalDateTime.now(), 2L, Set.of()
        );

        postulationDTO = new PostulationDTO(
                1L, "PENDING", LocalDateTime.now(), 1L, 0, offerDTO, null
        );
    }

    @Test
    void getAllPostulations_shouldReturn200() throws Exception {
        when(postulationService.getAllPostulations()).thenReturn(List.of(postulationDTO));

        mockMvc.perform(get("/postulations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void getPostulationById_whenExists_shouldReturn200() throws Exception {
        when(postulationService.getPostulationById(1L)).thenReturn(Optional.of(postulationDTO));

        mockMvc.perform(get("/postulations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getPostulationById_whenNotExists_shouldReturn404() throws Exception {
        when(postulationService.getPostulationById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/postulations/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPostulationsByUserId_shouldReturn200() throws Exception {
        when(postulationService.getPostulationsByUserId(1L)).thenReturn(List.of(postulationDTO));

        mockMvc.perform(get("/postulations/worker/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].workerId").value(1));
    }

    @Test
    void getPostulationsByOfferId_shouldReturn200() throws Exception {
        when(postulationService.getPostulationsByJobOfferId(1L)).thenReturn(List.of(postulationDTO));

        mockMvc.perform(get("/postulations/offer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void createPostulation_shouldReturn201() throws Exception {
        when(postulationService.createPostulation(any(PostulationDTO.class))).thenReturn(postulationDTO);

        mockMvc.perform(post("/postulations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postulationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void updateStatus_shouldReturn200() throws Exception {
        PostulationDTO acceptedDTO = new PostulationDTO(
                1L, "ACCEPTED", LocalDateTime.now(), 1L, 0, postulationDTO.offer(), null
        );
        when(postulationService.updateStatus(1L, "ACCEPTED")).thenReturn(acceptedDTO);

        mockMvc.perform(patch("/postulations/1/status")
                        .param("status", "ACCEPTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void deletePostulation_shouldReturn204() throws Exception {
        doNothing().when(postulationService).deletePostulation(1L);

        mockMvc.perform(delete("/postulations/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getPostulationsByEmployerId_shouldReturn200() throws Exception {
        when(postulationService.getPostulationsByEmployerId(2L)).thenReturn(List.of(postulationDTO));

        mockMvc.perform(get("/postulations/employer/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }
}