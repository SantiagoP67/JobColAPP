package com.jobcol.backend.shared.dto;


import java.time.LocalDateTime;
import java.util.Set;

public record OfferDTO(
        Long id,
        String title,
        String description,
        String category,
        String location,
        Integer salaryRange,
        String status,
        LocalDateTime publicationDate,
        Long employerId,
        Set<Long> postulationIds
) {
}
