package com.jobcol.backend.UserService.service;

import java.util.List;
import java.util.Optional;

import com.jobcol.backend.shared.dto.ProfileDTO;


public interface ProfileService {

    List<ProfileDTO> getAllProfiles();

    ProfileDTO createProfile(ProfileDTO profileDTO);

    ProfileDTO updateProfile(Long id, ProfileDTO profileDTO);

    Optional<ProfileDTO> getProfileById(Long id);

    Optional<ProfileDTO> getProfileByUserId(Long userId);

    void deleteProfile(Long id);

}
