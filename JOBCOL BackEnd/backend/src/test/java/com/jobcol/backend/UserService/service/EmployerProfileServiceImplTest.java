package com.jobcol.backend.UserService.service;

import com.jobcol.backend.UserService.model.EmployerProfile;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.EmployerProfileRepository;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.UserService.service.impl.EmployerProfileServiceImpl;
import com.jobcol.backend.shared.dto.EmployerProfileDTO;
import com.jobcol.backend.shared.mappers.EmployerProfileMapper;
import jakarta.persistence.EntityNotFoundException;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployerProfileServiceImplTest {

    @Mock
    private EmployerProfileRepository employerProfileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmployerProfileServiceImpl employerProfileService;

    private User user;
    private EmployerProfile employerProfile;
    private EmployerProfileDTO employerProfileDTO;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("empleador@test.com")
                .build();

        employerProfile = new EmployerProfile();
        employerProfile.setCompanyName("TechCorp");
        employerProfile.setDescription("Empresa de tecnología");
        employerProfile.setLocation("Medellín");
        employerProfile.setAverageRating(0.0);
        employerProfile.setTotalJobsPosted(0);
        employerProfile.setUser(user);

        employerProfileDTO = EmployerProfileDTO.builder()
                .companyName("TechCorp")
                .description("Empresa de tecnología")
                .location("Medellín")
                .averageRating(0.0)
                .totalJobsPosted(0)
                .build();

        jwt = mock(Jwt.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        // lenient() evita UnnecessaryStubbingException en tests que no usan JWT
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        SecurityContextHolder.setContext(securityContext);
    }

    // ── createProfile ────────────────────────────────────────────

    @Test
    void createProfile_shouldCreateAndReturnDTO() {
        try (MockedStatic<EmployerProfileMapper> mapper = mockStatic(EmployerProfileMapper.class)) {
            when(jwt.getClaim("email")).thenReturn("empleador@test.com");
            when(userRepository.findByEmail("empleador@test.com")).thenReturn(Optional.of(user));
            when(employerProfileRepository.existsByUser(user)).thenReturn(false);
            mapper.when(() -> EmployerProfileMapper.toEntity(employerProfileDTO)).thenReturn(employerProfile);
            when(employerProfileRepository.save(employerProfile)).thenReturn(employerProfile);
            mapper.when(() -> EmployerProfileMapper.toDTO(employerProfile)).thenReturn(employerProfileDTO);

            EmployerProfileDTO result = employerProfileService.createProfile(employerProfileDTO);

            assertThat(result).isNotNull();
            assertThat(result.getCompanyName()).isEqualTo("TechCorp");
            verify(employerProfileRepository).save(employerProfile);
        }
    }

    @Test
    void createProfile_whenUserNotFound_shouldThrowException() {
        when(jwt.getClaim("email")).thenReturn("noexiste@test.com");
        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employerProfileService.createProfile(employerProfileDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    void createProfile_whenProfileAlreadyExists_shouldThrowException() {
        when(jwt.getClaim("email")).thenReturn("empleador@test.com");
        when(userRepository.findByEmail("empleador@test.com")).thenReturn(Optional.of(user));
        when(employerProfileRepository.existsByUser(user)).thenReturn(true);

        assertThatThrownBy(() -> employerProfileService.createProfile(employerProfileDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El usuario ya tiene perfil");
    }

    // ── getByUserId ──────────────────────────────────────────────

    @Test
    void getByUserId_whenExists_shouldReturnDTO() {
        try (MockedStatic<EmployerProfileMapper> mapper = mockStatic(EmployerProfileMapper.class)) {
            when(employerProfileRepository.findByUserId(1L)).thenReturn(Optional.of(employerProfile));
            mapper.when(() -> EmployerProfileMapper.toDTO(employerProfile)).thenReturn(employerProfileDTO);

            EmployerProfileDTO result = employerProfileService.getByUserId(1L);

            assertThat(result).isNotNull();
            assertThat(result.getCompanyName()).isEqualTo("TechCorp");
        }
    }

    @Test
    void getByUserId_whenNotFound_shouldThrowException() {
        when(employerProfileRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employerProfileService.getByUserId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Perfil no encontrado");
    }

    // ── updateProfile ────────────────────────────────────────────

    @Test
    void updateProfile_shouldUpdateAndReturnDTO() {
        try (MockedStatic<EmployerProfileMapper> mapper = mockStatic(EmployerProfileMapper.class)) {
            when(employerProfileRepository.findByUserId(1L)).thenReturn(Optional.of(employerProfile));
            mapper.when(() -> EmployerProfileMapper.updateEntityFromDTO(employerProfileDTO, employerProfile))
                    .thenAnswer(inv -> null);
            when(employerProfileRepository.save(employerProfile)).thenReturn(employerProfile);
            mapper.when(() -> EmployerProfileMapper.toDTO(employerProfile)).thenReturn(employerProfileDTO);

            EmployerProfileDTO result = employerProfileService.updateProfile(1L, employerProfileDTO);

            assertThat(result).isNotNull();
            verify(employerProfileRepository).save(employerProfile);
        }
    }

    @Test
    void updateProfile_whenNotFound_shouldThrowException() {
        when(employerProfileRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employerProfileService.updateProfile(99L, employerProfileDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Perfil no encontrado");
    }
}