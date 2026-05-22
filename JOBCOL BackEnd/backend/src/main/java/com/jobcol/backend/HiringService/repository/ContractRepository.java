package com.jobcol.backend.HiringService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.jobcol.backend.HiringService.model.Contract;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByPostulationId(Long postulationId);

    List<Contract> findByPostulationWorkerId(Long workerId);

    List<Contract> findByPostulationOfferEmployerId(Long employerId);
}
