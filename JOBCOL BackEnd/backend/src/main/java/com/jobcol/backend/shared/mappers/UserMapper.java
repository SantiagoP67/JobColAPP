package com.jobcol.backend.shared.mappers;

import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.shared.dto.UserDTO;
import com.jobcol.backend.OfferService.model.Offer;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null) return null;

        Long profileId = user.getProfile() != null
                ? user.getProfile().getId()
                : null;

        Set<Long> offerIds = user.getOffers() != null
                ? user.getOffers()
                        .stream()
                        .map(Offer::getId)
                        .collect(Collectors.toSet())
                : null;

        return UserDTO.builder()
                .id(user.getId())
                .keycloakUserId(user.getKeycloakUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .cedula(user.getCedula())
                .imgUrl(user.getImgUrl())
                .active(user.getActive())
                .creationDate(user.getCreationDate())
                .role(user.getRole())
                .phone(user.getPhone())
                .profileId(profileId)
                .offerIds(offerIds)
                .build();
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;

        return User.builder()
                .id(dto.getId())
                .keycloakUserId(dto.getKeycloakUserId())
                .email(dto.getEmail())
                .username(dto.getUsername())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .cedula(dto.getCedula())
                .imgUrl(dto.getImgUrl())
                .phone(dto.getPhone())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .creationDate(dto.getCreationDate() != null ? dto.getCreationDate() : null)
                .role(dto.getRole())
                .build();
    }
}