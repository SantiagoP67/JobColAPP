package com.jobcol.backend.HiringService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcol.backend.HiringService.service.ContractService;
import com.jobcol.backend.shared.dto.ContractDTO;
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

@WebMvcTest(ContractController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContractControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private ContractService contractService;
    @Autowired private ObjectMapper objectMapper;

    private ContractDTO contractDTO;

    @BeforeEach
    void setUp() {
        OfferDTO offerDTO = new OfferDTO(
                1L, "Dev Java", "desc", "TI", "Bogotá",
                5000000, "OPEN", LocalDateTime.now(), 2L, Set.of()
        );

        PostulationDTO postulationDTO = new PostulationDTO(
                1L, "PENDING", LocalDateTime.now(), 1L, 0, offerDTO, null
        );

        contractDTO = new ContractDTO(
                1L, LocalDateTime.now(), null,
                5000000, "PENDING", false, false, postulationDTO
        );
    }

    @Test
    void createContract_shouldReturn201() throws Exception {
        when(contractService.createContract(any(ContractDTO.class))).thenReturn(contractDTO);

        mockMvc.perform(post("/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contractDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void updateContractStatus_shouldReturn200() throws Exception {
        when(contractService.updateStatus(eq(1L), any())).thenReturn(contractDTO);

        mockMvc.perform(put("/contracts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"ACTIVE\""))
                .andExpect(status().isOk());
    }

    @Test
    void getAllContracts_shouldReturn200() throws Exception {
        when(contractService.getAllContracts()).thenReturn(List.of(contractDTO));

        mockMvc.perform(get("/contracts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getContractById_whenExists_shouldReturn200() throws Exception {
        when(contractService.getContractById(1L)).thenReturn(Optional.of(contractDTO));

        mockMvc.perform(get("/contracts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agreedAmount").value(5000000));
    }

    @Test
    void getContractById_whenNotExists_shouldReturn404() throws Exception {
        when(contractService.getContractById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/contracts/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getContractsByUserId_shouldReturn200() throws Exception {
        when(contractService.getContractsByUserId(1L)).thenReturn(List.of(contractDTO));

        mockMvc.perform(get("/contracts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void finishContract_shouldReturn200() throws Exception {
        ContractDTO finished = new ContractDTO(
                1L, contractDTO.startDate(), LocalDateTime.now(),
                5000000, "FINISHED", true, true, contractDTO.postulation()
        );
        when(contractService.finishContract(1L)).thenReturn(finished);

        mockMvc.perform(patch("/contracts/1/finish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINISHED"));
    }

    @Test
    void deleteContract_shouldReturn204() throws Exception {
        doNothing().when(contractService).deleteContract(1L);

        mockMvc.perform(delete("/contracts/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void acceptContract_shouldReturn200() throws Exception {
        ContractDTO active = new ContractDTO(
                1L, contractDTO.startDate(), null,
                5000000, "ACTIVE", false, false, contractDTO.postulation()
        );
        when(contractService.acceptContract(1L)).thenReturn(active);

        mockMvc.perform(put("/contracts/1/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void rejectContract_shouldReturn200() throws Exception {
        ContractDTO rejected = new ContractDTO(
                1L, contractDTO.startDate(), null,
                5000000, "REJECTED", false, false, contractDTO.postulation()
        );
        when(contractService.rejectContract(1L)).thenReturn(rejected);

        mockMvc.perform(put("/contracts/1/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void requestFinishContract_shouldReturn200() throws Exception {
        ContractDTO pendingFinish = new ContractDTO(
                1L, contractDTO.startDate(), null,
                5000000, "PENDING_FINISH", true, false, contractDTO.postulation()
        );
        when(contractService.requestFinishContract(1L, 1L)).thenReturn(pendingFinish);

        mockMvc.perform(put("/contracts/1/finish-request")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_FINISH"));
    }
}