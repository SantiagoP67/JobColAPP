package com.jobcol.backend.shared.dto;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO{
        private Long id;
        private String keycloakUserId;
        private String email;
        private String username;
        private String firstName;
        private String lastName;
        private String cedula;
        private String imgUrl;
        private Boolean active;
        private LocalDateTime creationDate;
        private String role;
        private String phone;
        private Long profileId;
        private Set<Long> offerIds;
}