package com.jobcol.backend.HiringService.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.jobcol.backend.HiringService.model.Contract;
import com.jobcol.backend.HiringService.repository.ContractRepository;
import com.jobcol.backend.HiringService.service.ContractService;
import com.jobcol.backend.PostulationService.model.Postulation;
import com.jobcol.backend.PostulationService.repository.PostulationRepository;
import com.jobcol.backend.shared.dto.ContractDTO;
import com.jobcol.backend.shared.mappers.ContractMapper;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final PostulationRepository postulationRepository;

    @Override
    @Transactional
    public ContractDTO createContract(ContractDTO contractDTO) {

        Contract contract = ContractMapper.toEntity(contractDTO);

        if (contract.getStartDate() == null) {
            contract.setStartDate(LocalDateTime.now());
        }

        if (contract.getStatus() == null) {
            contract.setStatus("PENDING");
        }

        Long postulationId = contractDTO.postulation().id();

        Postulation postulation = postulationRepository
                .findById(postulationId)
                .orElseThrow(() ->
                        new RuntimeException("Postulation not found"));

        contract.setPostulation(postulation);
        contract.setWorkerFinished(false);
        contract.setEmployerFinished(false);

        Contract savedContract = contractRepository.save(contract);

        postulation.setContract(savedContract);

        postulationRepository.save(postulation);

        return ContractMapper.toDTO(savedContract);
    }

    @Override
    public ContractDTO updateStatus(Long id, String status) {

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Contract not found"));

        contract.setStatus(status);

        return ContractMapper.toDTO(
                contractRepository.save(contract)
        );
    }

    @Override
    public Optional<ContractDTO> getContractById(Long id) {

        return contractRepository.findById(id)
                .map(ContractMapper::toDTO);
    }

    @Override
    public Optional<ContractDTO> getContractByPostulationId(Long postulationId) {

        return contractRepository.findByPostulationId(postulationId)
                .map(ContractMapper::toDTO);
    }

    @Override
    public List<ContractDTO> getAllContracts() {

        return contractRepository.findAll()
                .stream()
                .map(ContractMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractDTO> getContractsByUserId(Long userId) {

        List<Contract> workerContracts =
                contractRepository.findByPostulationWorkerId(userId);

        List<Contract> employerContracts =
                contractRepository.findByPostulationOfferEmployerId(userId);

        List<Contract> allContracts = new ArrayList<>();

        allContracts.addAll(workerContracts);

        employerContracts.forEach(contract -> {

            boolean exists = allContracts.stream()
                    .anyMatch(c -> c.getId().equals(contract.getId()));

            if (!exists) {
                allContracts.add(contract);
            }
        });

        return allContracts.stream()
                .map(ContractMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ContractDTO finishContract(Long id) {

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Contract not found"));

        contract.setStatus("FINISHED");
        contract.setEndDate(LocalDateTime.now());

        return ContractMapper.toDTO(
                contractRepository.save(contract)
        );
    }

    @Override
    public void deleteContract(Long id) {

        contractRepository.deleteById(id);
    }

    @Override
    public ContractDTO acceptContract(Long id) {

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        contract.setStatus("ACTIVE");

        return ContractMapper.toDTO(
                contractRepository.save(contract)
        );
    }

    @Override
    public ContractDTO rejectContract(Long id) {

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        contract.setStatus("REJECTED");

        return ContractMapper.toDTO(
                contractRepository.save(contract)
        );
    }

    @Override
    public ContractDTO requestFinishContract(
            Long contractId,
            Long userId
    ) {

        Contract contract =
                contractRepository.findById(contractId)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Contract not found"
                    )
                );

        Long workerId =
                contract.getPostulation()
                        .getWorker()
                        .getId();

        Long employerId =
                contract.getPostulation()
                        .getOffer()
                        .getEmployer()
                        .getId();

        if (userId.equals(workerId)) {

            contract.setWorkerFinished(true);

        } else if (userId.equals(employerId)) {

            contract.setEmployerFinished(true);

        }

        if (
            Boolean.TRUE.equals(
                contract.getWorkerFinished()
            )
            &&
            Boolean.TRUE.equals(
                contract.getEmployerFinished()
            )
        ) {

            contract.setStatus("FINISHED");

            contract.setEndDate(
                LocalDateTime.now()
            );

        } else {

            contract.setStatus(
                "PENDING_FINISH"
            );
        }

        return ContractMapper.toDTO(
            contractRepository.save(contract)
        );
    }

    @Override
    public ContractDTO confirmFinishContract(
            Long contractId,
            Long userId
    ) {

        Contract contract =
                contractRepository.findById(contractId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Contract not found"
                        )
                );

        Long workerId =
                contract.getPostulation()
                        .getWorker()
                        .getId();

        Long employerId =
                contract.getPostulation()
                        .getOffer()
                        .getEmployer()
                        .getId();

        if (userId.equals(workerId)) {

            contract.setWorkerFinished(true);

        } else if (userId.equals(employerId)) {

            contract.setEmployerFinished(true);

        } else {

            throw new RuntimeException(
                    "User not part of contract"
            );
        }

        if (
            Boolean.TRUE.equals(contract.getWorkerFinished())
            &&
            Boolean.TRUE.equals(contract.getEmployerFinished())
        ) {

            contract.setStatus("FINISHED");

            contract.setEndDate(LocalDateTime.now());

        } else {

            contract.setStatus("PENDING_FINISH");
        }

        return ContractMapper.toDTO(
                contractRepository.save(contract)
        );
    }
}