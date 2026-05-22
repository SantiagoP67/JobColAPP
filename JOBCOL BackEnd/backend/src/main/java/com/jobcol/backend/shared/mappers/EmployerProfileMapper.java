package com.jobcol.backend.shared.mappers;

import com.jobcol.backend.UserService.model.EmployerProfile;
import com.jobcol.backend.shared.dto.EmployerProfileDTO;

public class EmployerProfileMapper {

    public static EmployerProfileDTO toDTO(EmployerProfile entity) {
        if (entity == null) return null;

        return EmployerProfileDTO.builder()
                .companyName(entity.getCompanyName())
                .description(entity.getDescription())
                .location(entity.getLocation())
                .averageRating(entity.getAverageRating())
                .totalJobsPosted(entity.getTotalJobsPosted())
                .totalReviews(entity.getTotalReviews())
                .build();
    }

    public static EmployerProfile toEntity(EmployerProfileDTO dto) {
        if (dto == null) return null;

        return EmployerProfile.builder()
                .companyName(dto.getCompanyName())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .averageRating(dto.getAverageRating())
                .totalJobsPosted(dto.getTotalJobsPosted())
                .totalReviews(dto.getTotalReviews())
                .build();
    }

    public static void updateEntityFromDTO(EmployerProfileDTO dto, EmployerProfile entity) {
        if (dto == null || entity == null) return;

        entity.setCompanyName(dto.getCompanyName());
        entity.setDescription(dto.getDescription());
        entity.setLocation(dto.getLocation());
        entity.setAverageRating(dto.getAverageRating());
        entity.setTotalJobsPosted(dto.getTotalJobsPosted());
        entity.setTotalReviews(dto.getTotalReviews());
    }
}
