package com.jobcol.backend.shared.mappers;


import java.util.Set;
import java.util.stream.Collectors;

import com.jobcol.backend.OfferService.model.Offer;
import com.jobcol.backend.PostulationService.model.Postulation;
import com.jobcol.backend.shared.dto.OfferDTO;

public class OfferMapper {

    // Entity → DTO
    public static OfferDTO toDTO(Offer offer) {
        if (offer == null) {
            return null;
        }

        Long employerId = offer.getEmployer() != null
                ? offer.getEmployer().getId()
                : null;

        Set<Long> postulationIds = offer.getPostulations() != null
                ? offer.getPostulations()
                        .stream()
                        .map(Postulation::getId)
                        .collect(Collectors.toSet())
                : null;

        return new OfferDTO(
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getCategory(),
                offer.getLocation(),
                offer.getSalaryRange(),
                offer.getStatus(),
                offer.getPublicationDate(),
                employerId,
                postulationIds
        );
    }

    // DTO → Entity
    public static Offer toEntity(OfferDTO dto) {
        if (dto == null) {
            return null;
        }

        return Offer.builder()
                .id(dto.id())
                .title(dto.title())
                .description(dto.description())
                .category(dto.category())
                .location(dto.location())
                .salaryRange(dto.salaryRange())
                .status(dto.status())
                .publicationDate(dto.publicationDate())
                
                .build();
    }
}
