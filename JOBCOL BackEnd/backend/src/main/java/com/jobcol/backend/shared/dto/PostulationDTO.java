package com.jobcol.backend.shared.dto;

import java.time.LocalDateTime;

public record PostulationDTO(
        Long id,
        String status,
        LocalDateTime applicationDate,
        Long workerId,
        int calification,
        OfferDTO offer,
        Long contractId
) {
}
