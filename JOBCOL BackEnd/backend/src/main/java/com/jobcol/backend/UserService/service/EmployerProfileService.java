package com.jobcol.backend.UserService.service;

import com.jobcol.backend.shared.dto.EmployerProfileDTO;

public interface EmployerProfileService {
    EmployerProfileDTO createProfile(EmployerProfileDTO dto);

    EmployerProfileDTO getByUserId(Long userId);

    EmployerProfileDTO updateProfile(Long userId, EmployerProfileDTO dto);
}
