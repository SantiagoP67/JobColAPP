package com.jobcol.backend.UserService.service;

import com.jobcol.backend.UserService.model.Profile;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.ProfileRepository;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.UserService.service.impl.ProfileServiceImpl;
import com.jobcol.backend.shared.dto.ProfileDTO;
import com.jobcol.backend.shared.mappers.ProfileMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private ProfileServiceImpl profileService;

    private User user;
    private Profile profile;
    private ProfileDTO profileDTO;

    // Helper: mockea SecurityContextHolder con un JWT que retorna email o username
    private void mockSecurityContext(String email, String username) {
        Jwt jwt = mock(Jwt.class);
        lenient().when(jwt.getClaim("email")).thenReturn(email);
        lenient().when(jwt.getClaim("preferred_username")).thenReturn(username);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(securityContext);
    }

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("santiago@test.com")
                .username("santi")
                .build();

        profile = new Profile();
        profile.setId(1L);
        profile.setSkills("Java, Spring");
        profile.setExperience("2 años");
        profile.setLocation("Bogotá");
        profile.setVisible(true);
        profile.setUser(user);

        profileDTO = ProfileDTO.builder()
                .id(1L)
                .skills("Java, Spring")
                .experience("2 años")
                .location("Bogotá")
                .visible(true)
                .userId(1L)
                .build();
    }

    // ── getAllProfiles ───────────────────────────────────────────

    @Test
    void getAllProfiles_shouldReturnList() {
        try (MockedStatic<ProfileMapper> mapper = mockStatic(ProfileMapper.class)) {
            when(profileRepository.findAll()).thenReturn(List.of(profile));
            mapper.when(() -> ProfileMapper.toDTO(profile)).thenReturn(profileDTO);

            List<ProfileDTO> result = profileService.getAllProfiles();

            assertThat(result).hasSize(1);
        }
    }

    @Test
    void getAllProfiles_whenEmpty_shouldReturnEmptyList() {
        when(profileRepository.findAll()).thenReturn(List.of());

        List<ProfileDTO> result = profileService.getAllProfiles();

        assertThat(result).isEmpty();
    }

    // ── createProfile ────────────────────────────────────────────

    @Test
    void createProfile_shouldCreateAndReturnDTO() {
        try (MockedStatic<ProfileMapper> mapper = mockStatic(ProfileMapper.class)) {
            mockSecurityContext("santiago@test.com", "santi");
            when(userRepository.findByEmail("santiago@test.com")).thenReturn(Optional.of(user));
            when(profileRepository.existsByUser(user)).thenReturn(false);
            mapper.when(() -> ProfileMapper.toEntity(profileDTO)).thenReturn(profile);
            mapper.when(() -> ProfileMapper.toDTO(profile)).thenReturn(profileDTO);
            when(profileRepository.save(profile)).thenReturn(profile);

            ProfileDTO result = profileService.createProfile(profileDTO);

            assertThat(result).isNotNull();
            verify(profileRepository).save(profile);
        }
    }

    @Test
    void createProfile_whenUserAlreadyHasProfile_shouldThrowException() {
        mockSecurityContext("santiago@test.com", "santi");
        when(userRepository.findByEmail("santiago@test.com")).thenReturn(Optional.of(user));
        when(profileRepository.existsByUser(user)).thenReturn(true);

        assertThatThrownBy(() -> profileService.createProfile(profileDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El usuario ya tiene perfil");
    }

    @Test
    void createProfile_whenUserNotFound_shouldThrowException() {
        mockSecurityContext("noexiste@test.com", "noexiste");
        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.createProfile(profileDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    // ── updateProfile ────────────────────────────────────────────

    @Test
    void updateProfile_shouldUpdateAndReturnDTO() {
        try (MockedStatic<ProfileMapper> mapper = mockStatic(ProfileMapper.class)) {
            mockSecurityContext("santiago@test.com", "santi");
            when(userRepository.findByUsername("santi")).thenReturn(Optional.of(user));
            when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
            mapper.when(() -> ProfileMapper.toDTO(profile)).thenReturn(profileDTO);
            when(profileRepository.save(profile)).thenReturn(profile);

            ProfileDTO result = profileService.updateProfile(1L, profileDTO);

            assertThat(result).isNotNull();
            verify(profileRepository).save(profile);
        }
    }

    @Test
    void updateProfile_whenNotOwner_shouldThrowException() {
        User otherUser = User.builder().id(99L).username("otro").build();
        mockSecurityContext("otro@test.com", "otro");
        when(userRepository.findByUsername("otro")).thenReturn(Optional.of(otherUser));
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        assertThatThrownBy(() -> profileService.updateProfile(1L, profileDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No autorizado");
    }

    @Test
    void updateProfile_whenProfileNotFound_shouldThrowException() {
        mockSecurityContext("santiago@test.com", "santi");
        when(userRepository.findByUsername("santi")).thenReturn(Optional.of(user));
        when(profileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.updateProfile(99L, profileDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Profile not found");
    }

    // ── getProfileById ───────────────────────────────────────────

    @Test
    void getProfileById_whenExists_shouldReturnDTO() {
        try (MockedStatic<ProfileMapper> mapper = mockStatic(ProfileMapper.class)) {
            when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
            mapper.when(() -> ProfileMapper.toDTO(profile)).thenReturn(profileDTO);

            Optional<ProfileDTO> result = profileService.getProfileById(1L);

            assertThat(result).isPresent();
        }
    }

    @Test
    void getProfileById_whenNotExists_shouldReturnEmpty() {
        when(profileRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ProfileDTO> result = profileService.getProfileById(99L);

        assertThat(result).isEmpty();
    }

    // ── getProfileByUserId ───────────────────────────────────────

    @Test
    void getProfileByUserId_whenExists_shouldReturnDTO() {
        try (MockedStatic<ProfileMapper> mapper = mockStatic(ProfileMapper.class)) {
            when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
            mapper.when(() -> ProfileMapper.toDTO(profile)).thenReturn(profileDTO);

            Optional<ProfileDTO> result = profileService.getProfileByUserId(1L);

            assertThat(result).isPresent();
        }
    }

    // ── deleteProfile ────────────────────────────────────────────

    @Test
    void deleteProfile_shouldCallDeleteById() {
        doNothing().when(profileRepository).deleteById(1L);

        profileService.deleteProfile(1L);

        verify(profileRepository).deleteById(1L);
    }
}