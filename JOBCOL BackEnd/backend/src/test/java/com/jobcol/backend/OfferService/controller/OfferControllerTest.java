package com.jobcol.backend.OfferService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcol.backend.OfferService.service.OfferService;
import com.jobcol.backend.shared.dto.OfferDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfferController.class)
@AutoConfigureMockMvc(addFilters = false)
class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OfferService offerService;

    @Autowired
    private ObjectMapper objectMapper;

    private OfferDTO offerDTO;

    @BeforeEach
    void setUp() {
        offerDTO = new OfferDTO(
                1L,
                "Desarrollador Java",
                "Se busca dev Java",
                "TI",
                "Bogotá",
                5000000,
                "OPEN",
                LocalDateTime.now(),
                1L,
                Set.of()
        );
    }

    // ── GET /offers ──────────────────────────────────────────────

    @Test
    void getAllOffers_shouldReturn200() throws Exception {
        when(offerService.getAllOffers()).thenReturn(List.of(offerDTO));

        mockMvc.perform(get("/offers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Desarrollador Java"));
    }

    // ── GET /offers/active ───────────────────────────────────────

    @Test
    void getActiveOffers_shouldReturn200() throws Exception {
        when(offerService.getActiveOffers()).thenReturn(List.of(offerDTO));

        mockMvc.perform(get("/offers/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    // ── GET /offers/{id} ─────────────────────────────────────────

    @Test
    void getOfferById_whenExists_shouldReturn200() throws Exception {
        when(offerService.getOfferById(1L)).thenReturn(Optional.of(offerDTO));

        mockMvc.perform(get("/offers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Desarrollador Java"));
    }

    @Test
    void getOfferById_whenNotExists_shouldReturn404() throws Exception {
        when(offerService.getOfferById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/offers/99"))
                .andExpect(status().isNotFound());
    }

    // ── GET /offers/employer/{userId} ────────────────────────────

    @Test
    void getOffersByUserId_shouldReturn200() throws Exception {
        when(offerService.getOffersByUserId(1L)).thenReturn(List.of(offerDTO));

        mockMvc.perform(get("/offers/employer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employerId").value(1));
    }

    // ── POST /offers ─────────────────────────────────────────────

    @Test
    void createOffer_shouldReturn201() throws Exception {
        when(offerService.createOffer(any(OfferDTO.class))).thenReturn(offerDTO);

        mockMvc.perform(post("/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Desarrollador Java"));
    }

    // ── PUT /offers/{id} ─────────────────────────────────────────

    @Test
    void updateOffer_shouldReturn200() throws Exception {
        when(offerService.updateOffer(eq(1L), any(OfferDTO.class))).thenReturn(offerDTO);

        mockMvc.perform(put("/offers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Bogotá"));
    }

    // ── PATCH /offers/{id}/close ─────────────────────────────────

    @Test
    void closeOffer_shouldReturn200WithClosedStatus() throws Exception {
        OfferDTO closedDTO = new OfferDTO(
                1L, "Desarrollador Java", "desc", "TI",
                "Bogotá", 5000000, "CLOSED", LocalDateTime.now(), 1L, Set.of()
        );
        when(offerService.closeOffer(1L)).thenReturn(closedDTO);

        mockMvc.perform(patch("/offers/1/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }

    // ── DELETE /offers/{id} ──────────────────────────────────────

    @Test
    void deleteOffer_shouldReturn204() throws Exception {
        doNothing().when(offerService).deleteOffer(1L);

        mockMvc.perform(delete("/offers/1"))
                .andExpect(status().isNoContent());
    }
}