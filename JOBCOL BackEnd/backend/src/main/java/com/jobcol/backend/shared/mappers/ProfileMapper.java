package com.jobcol.backend.shared.mappers;

import com.jobcol.backend.UserService.model.Profile;
import com.jobcol.backend.shared.dto.ProfileDTO;

public class ProfileMapper {

    public static ProfileDTO toDTO(Profile profile) {

        if (profile == null) {
            return null;
        }

        Long userId = profile.getUser() != null
                ? profile.getUser().getId()
                : null;

        return ProfileDTO.builder()
                .id(profile.getId())
                .skills(profile.getSkills())
                .experience(profile.getExperience())
                .location(profile.getLocation())
                .visible(profile.getVisible())
                .averageRating(profile.getAverageRating())
                .totalReviews(profile.getTotalReviews())
                .userId(userId)
                .build();
    }

    
    public static Profile toEntity(ProfileDTO dto) {

        if (dto == null) {
            return null;
        }

        return Profile.builder()
                .id(dto.getId())
                .skills(dto.getSkills())
                .experience(dto.getExperience())
                .location(dto.getLocation())
                .visible(dto.getVisible())
                .averageRating(dto.getAverageRating())
                .totalReviews(dto.getTotalReviews())
                .build();
    }
}