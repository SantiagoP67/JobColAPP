package com.jobcol.backend.HiringService.service;

import java.util.List;
import java.util.Optional;

import com.jobcol.backend.shared.dto.ContractDTO;

public interface ContractService {

    ContractDTO createContract(ContractDTO contractDTO);

    ContractDTO updateStatus(Long id, String status);

    Optional<ContractDTO> getContractById(Long id);

    Optional<ContractDTO> getContractByPostulationId(Long postulationId);

    List<ContractDTO> getAllContracts();

    List<ContractDTO> getContractsByUserId(Long userId);

    ContractDTO finishContract(Long id);

    void deleteContract(Long id);

    ContractDTO acceptContract(Long id);

    ContractDTO rejectContract(Long id);

    ContractDTO requestFinishContract(Long contractId,Long userId);

    ContractDTO confirmFinishContract(Long contractId, Long userId);

}
