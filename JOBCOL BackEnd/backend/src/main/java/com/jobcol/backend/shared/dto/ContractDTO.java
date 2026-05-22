package com.jobcol.backend.shared.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record ContractDTO(
        Long id,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer agreedAmount,
        String status,
        Boolean workerFinished,
        Boolean employerFinished,
        PostulationDTO postulation
) {
}