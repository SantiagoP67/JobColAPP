package com.jobcol.backend.UserService.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.jobcol.backend.UserService.model.Profile;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.ProfileRepository;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.UserService.service.ProfileService;
import com.jobcol.backend.shared.dto.ProfileDTO;
import com.jobcol.backend.shared.mappers.ProfileMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository; 

    @Override
    public List<ProfileDTO> getAllProfiles() {
        return profileRepository.findAll().stream()
                .map(ProfileMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProfileDTO createProfile(ProfileDTO profileDTO) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = extractEmail(auth);

        System.out.println("EMAIL DEL TOKEN: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (profileRepository.existsByUser(user)) {
            throw new RuntimeException("El usuario ya tiene perfil");
        }

        Profile profile = ProfileMapper.toEntity(profileDTO);

        profile.setUser(user); 

        return ProfileMapper.toDTO(profileRepository.save(profile));
    }


    @Override
    public ProfileDTO updateProfile(Long id, ProfileDTO profileDTO) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = extractUsername(auth);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        
        if (!profile.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No autorizado");
        }

        profile.setSkills(profileDTO.getSkills());
        profile.setExperience(profileDTO.getExperience());
        profile.setLocation(profileDTO.getLocation());
        profile.setVisible(profileDTO.getVisible());

        return ProfileMapper.toDTO(profileRepository.save(profile));
    }


    @Override
    public Optional<ProfileDTO> getProfileById(Long id) {
        return profileRepository.findById(id)
                .map(ProfileMapper::toDTO);
    }


    @Override
    public Optional<ProfileDTO> getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .map(ProfileMapper::toDTO);
    }

    @Override
    public void deleteProfile(Long id) {
        profileRepository.deleteById(id);
    }

    private String extractUsername(Authentication auth) {

        Object principal = auth.getPrincipal();

        if (principal instanceof Jwt jwt) {
            return jwt.getClaim("preferred_username"); // 🔥 clave
        }

        return auth.getName();
    }

    private String extractEmail(Authentication auth) {

        Object principal = auth.getPrincipal();

        if (principal instanceof Jwt jwt) {

            System.out.println("JWT COMPLETO: " + jwt.getClaims());

            return jwt.getClaim("email"); 
        }

        return auth.getName();
    }
}