package com.jobcol.backend.HiringService.service;

import com.jobcol.backend.HiringService.model.Contract;
import com.jobcol.backend.HiringService.repository.ContractRepository;
import com.jobcol.backend.HiringService.service.impl.ContractServiceImpl;
import com.jobcol.backend.OfferService.model.Offer;
import com.jobcol.backend.PostulationService.model.Postulation;
import com.jobcol.backend.PostulationService.repository.PostulationRepository;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.shared.dto.ContractDTO;
import com.jobcol.backend.shared.dto.OfferDTO;
import com.jobcol.backend.shared.dto.PostulationDTO;
import com.jobcol.backend.shared.mappers.ContractMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceImplTest {

    @Mock private ContractRepository contractRepository;
    @Mock private PostulationRepository postulationRepository;

    @InjectMocks
    private ContractServiceImpl contractService;

    private User worker;
    private User employer;
    private Offer offer;
    private Postulation postulation;
    private Contract contract;
    private ContractDTO contractDTO;
    private PostulationDTO postulationDTO;
    private OfferDTO offerDTO;

    @BeforeEach
    void setUp() {
        worker = User.builder().id(1L).username("worker").build();
        employer = User.builder().id(2L).username("employer").build();

        offer = new Offer();
        offer.setId(1L);
        offer.setEmployer(employer);

        postulation = Postulation.builder()
                .id(1L)
                .status("PENDING")
                .worker(worker)
                .offer(offer)
                .build();

        contract = new Contract();
        contract.setId(1L);
        contract.setStatus("PENDING");
        contract.setStartDate(LocalDateTime.now());
        contract.setAgreedAmount(5000000);
        contract.setWorkerFinished(false);
        contract.setEmployerFinished(false);
        contract.setPostulation(postulation);

        offerDTO = new OfferDTO(
                1L, "Dev Java", "desc", "TI", "Bogotá",
                5000000, "OPEN", LocalDateTime.now(), 2L, Set.of()
        );

        postulationDTO = new PostulationDTO(
                1L, "PENDING", LocalDateTime.now(), 1L, 0, offerDTO, null
        );

        contractDTO = new ContractDTO(
                1L, LocalDateTime.now(), null,
                5000000, "PENDING", false, false, postulationDTO
        );
    }

    // ── createContract ───────────────────────────────────────────

    @Test
    void createContract_shouldCreateAndReturnDTO() {
        try (MockedStatic<ContractMapper> mapper = mockStatic(ContractMapper.class)) {
            mapper.when(() -> ContractMapper.toEntity(contractDTO)).thenReturn(contract);
            when(postulationRepository.findById(1L)).thenReturn(Optional.of(postulation));
            when(contractRepository.save(contract)).thenReturn(contract);
            when(postulationRepository.save(postulation)).thenReturn(postulation);
            mapper.when(() -> ContractMapper.toDTO(contract)).thenReturn(contractDTO);

            ContractDTO result = contractService.createContract(contractDTO);

            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo("PENDING");
            verify(contractRepository).save(contract);
            verify(postulationRepository).save(postulation);
        }
    }

    @Test
    void createContract_whenPostulationNotFound_shouldThrowException() {
        try (MockedStatic<ContractMapper> mapper = mockStatic(ContractMapper.class)) {
            mapper.when(() -> ContractMapper.toEntity(contractDTO)).thenReturn(contract);
            when(postulationRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> contractService.createContract(contractDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Postulation not found");
        }
    }

    // ── updateStatus ─────────────────────────────────────────────

    @Test
    void updateStatus_shouldUpdateAndReturnDTO() {
        try (MockedStatic<ContractMapper> mapper = mockStatic(ContractMapper.class)) {
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
            when(contractRepository.save(contract)).thenReturn(contract);
            mapper.when(() -> ContractMapper.toDTO(contract)).thenReturn(contractDTO);

            ContractDTO result = contractService.updateStatus(1L, "ACTIVE");

            assertThat(contract.getStatus()).isEqualTo("ACTIVE");
            assertThat(result).isNotNull();
            verify(contractRepository).save(contract);
        }
    }

    @Test
    void updateStatus_whenNotFound_shouldThrowException() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.updateStatus(99L, "ACTIVE"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Contract not found");
    }

    // ── getContractById ──────────────────────────────────────────

    @Test
    void getContractById_whenExists_shouldReturnDTO() {
        try (MockedStatic<ContractMapper> mapper = mockStatic(ContractMapper.class)) {
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
            mapper.when(() -> ContractMapper.toDTO(contract)).thenReturn(contractDTO);

            Optional<ContractDTO> result = contractService.getContractById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().id()).isEqualTo(1L);
        }
    }

    @Test
    void getContractById_whenNotExists_shouldReturnEmpty() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ContractDTO> result = contractService.getContractById(99L);

        assertThat(result).isEmpty();
    }

    // ── getContractByPostulationId ───────────────────────────────

    @Test
    void getContractByPostulationId_whenExists_shouldReturnDTO() {
        try (MockedStatic<ContractMapper> mapper = mockStatic(ContractMapper.class)) {
            when(contractRepository.findByPostulationId(1L)).thenReturn(Optional.of(contract));
            mapper.when(() -> ContractMapper.toDTO(contract)).thenReturn(contractDTO);

            Optional<ContractDTO> result = contractService.getContractByPostulationId(1L);

            assertThat(result).isPresent();
        }
    }

    @Test
    void getContractByPostulationId_whenNotExists_shouldReturnEmpty() {
        when(contractRepository.findByPostulationId(99L)).thenReturn(Optional.empty());

        Optional<ContractDTO> result = contractService.getContractByPostulationId(99L);

        assertThat(result).isEmpty();
    }

    // ── getAllContracts ──────────────────────────────────────────

    @Test
    void getAllContracts_shouldReturnAll() {
        try (MockedStatic<ContractMapper> mapper = mockStatic(ContractMapper.class)) {
            when(contractRepository.findAll()).thenReturn(List.of(contract));
            mapper.when(() -> ContractMapper.toDTO(contract)).thenReturn(contractDTO);

            List<ContractDTO> result = contractService.getAllContracts();

            assertThat(result).hasSize(1);
        }
    }

    @Test
    void getAllContracts_whenEmpty_shouldReturnEmptyList() {
        when(contractRepository.findAll()).thenReturn(List.of());

        List<ContractDTO> result = contractService.getAllContracts();

        assertThat(result).isEmpty();
    }

    // ── getContractsByUserId ─────────────────────────────────────

    @Test
    void getContractsByUserId_shouldMergeWorkerAndEmployerContracts() {
        try (MockedStatic<ContractMapper> mapper = mockStatic(ContractMapper.class)) {
            Contract employerContract = new Contract();
            employerContract.setId(2L);
            employerContract.setStatus("ACTIVE");
            employerContract.setPostulation(postulation);

            ContractDTO employerContractDTO = new ContractDTO(
                    2L, LocalDateTime.now(), null, 3000000,
                    "ACTIVE", false, false,
                    new PostulationDTO(1L, "PENDING", LocalDateTime.now(), 1L, 0, offerDTO, null)
            );

            when(contractRepository.findByPostulationWorkerId(1L)).thenReturn(List.of(contract));
            when(contractRepository.findByPostulationOfferEmployerId(1L)).thenReturn(List.of(employerContract));
            mapper.when(() -> ContractMapper.toDTO(contract)).thenReturn(contractDTO);
            mapper.when(() -> ContractMapper.toDTO(employerContract)).thenReturn(employerContractDTO);

            List<ContractDTO> result = contractService.getContractsByUserId(1L);

            assertThat(result).hasSize(2);
        }
    }

    @Test
    void getContractsByUserId_shouldNotDuplicateContracts() {
        try (MockedStatic<ContractMapper> mapper = mockStatic(ContractMapper.class)) {
            when(contractRepository.findByPostulationWorkerId(1L)).thenReturn(List.of(contract));
            when(contractRepository.findByPostulationOfferEmployerId(1L)).thenReturn(List.of(contract));
            mapper.when(() -> ContractMapper.toDTO(contract)).thenReturn(contractDTO);

            List<ContractDTO> result = contractService.getContractsByUserId(1L);

            assertThat(result).hasSize(1);
        }
    }

    // ── finishContract ───────────────────────────────────────────

    @Test
    void finishContract_shouldSetStatusFinishedAndReturnDTO() {
        try (MockedStatic<ContractMapper> mapper = mockStatic(ContractMapper.class)) {
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
            when(contractRepository.save(contract)).thenReturn(contract);
            mapper.when(() -> ContractMapper.toDTO(contract)).thenReturn(contractDTO);

            ContractDTO result = contractService.finishContract(1L);

            assertThat(contract.getStatus()).isEqualTo("FINISHED");
            assertThat(contract.getEndDate()).isNotNull();
            assertThat(result).isNotNull();
        }
    }

    @Test
    void finishContract_whenNotFound_shouldThrowException() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.finishContract(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Contract not found");
    }

    // ── deleteContract ───────────────────────────────────────────

    @Test
    void deleteContract_shouldCallDeleteById() {
        doNothing().when(contractRepository).deleteById(1L);

        contractService.deleteContract(1L);

        verify(contractRepository).deleteById(1L);
    }

    // ── acceptContract ───────────────────────────────────────────

    @Test
    void acceptContract_shouldSetStatusActiveAndReturnDTO() {
        try (MockedStatic<ContractMapper> mapper = mockStatic(ContractMapper.class)) {
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
            when(contractRepository.save(contract)).thenReturn(contract);
            mapper.when(() -> ContractMapper.toDTO(contract)).thenReturn(contractDTO);

            ContractDTO result = contractService.acceptContract(1L);

            assertThat(contract.getStatus()).isEqualTo("ACTIVE");
            assertThat(result).isNotNull();
        }
    }

    @Test
    void acceptContract_whenNotFound_shouldThrowException() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.acceptContract(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Contract not found");
    }

    // ── rejectContract ───────────────────────────────────────────

    @Test
    void rejectContract_shouldSetStatusRejectedAndReturnDTO() {
        try (MockedStatic<ContractMapper> mapper = mockStatic(ContractMapper.class)) {
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
            when(contractRepository.save(contract)).thenReturn(contract);
            mapper.when(() -> ContractMapper.toDTO(contract)).thenReturn(contractDTO);

            ContractDTO result = contractService.rejectContract(1L);

            assertThat(contract.getStatus()).isEqualTo("REJECTED");
            assertThat(result).isNotNull();
        }
    }

    @Test
    void rejectContract_whenNotFound_shouldThrowException() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.rejectContract(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Contract not found");
    }

    // ── requestFinishContract ────────────────────────────────────

    @Test
    void requestFinishContract_whenWorkerRequests_shouldSetWorkerFinished() {
        try (MockedStatic<ContractMapper> mapper = mockStatic(ContractMapper.class)) {
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
            when(contractRepository.save(contract)).thenReturn(contract);
            mapper.when(() -> ContractMapper.toDTO(contract)).thenReturn(contractDTO);

            contractService.requestFinishContract(1L, 1L);

            assertThat(contract.getWorkerFinished()).isTrue();
            assertThat(contract.getStatus()).isEqualTo("PENDING_FINISH");
        }
    }

    @Test
    void requestFinishContract_whenEmployerRequests_shouldSetEmployerFinished() {
        try (MockedStatic<ContractMapper> mapper = mockStatic(ContractMapper.class)) {
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
            when(contractRepository.save(contract)).thenReturn(contract);
            mapper.when(() -> ContractMapper.toDTO(contract)).thenReturn(contractDTO);

            contractService.requestFinishContract(1L, 2L);

            assertThat(contract.getEmployerFinished()).isTrue();
            assertThat(contract.getStatus()).isEqualTo("PENDING_FINISH");
        }
    }

    @Test
    void requestFinishContract_whenBothFinish_shouldSetStatusFinished() {
        try (MockedStatic<ContractMapper> mapper = mockStatic(ContractMapper.class)) {
            contract.setWorkerFinished(true);
            when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
            when(contractRepository.save(contract)).thenReturn(contract);
            mapper.when(() -> ContractMapper.toDTO(contract)).thenReturn(contractDTO);

            contractService.requestFinishContract(1L, 2L);

            assertThat(contract.getWorkerFinished()).isTrue();
            assertThat(contract.getEmployerFinished()).isTrue();
            assertThat(contract.getStatus()).isEqualTo("FINISHED");
            assertThat(contract.getEndDate()).isNotNull();
        }
    }

    @Test
    void requestFinishContract_whenNotFound_shouldThrowException() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.requestFinishContract(99L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Contract not found");
    }
}