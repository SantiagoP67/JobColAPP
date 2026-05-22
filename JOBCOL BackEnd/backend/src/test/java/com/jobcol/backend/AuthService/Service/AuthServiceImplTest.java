package com.jobcol.backend.AuthService.Service;

import com.jobcol.backend.AuthService.model.VerificationCode;
import com.jobcol.backend.AuthService.repository.verificationRepository;
import com.jobcol.backend.AuthService.service.impl.AuthServiceImpl;
import com.jobcol.backend.NotificationService.service.NotificationService;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.UserService.service.UserService;
import com.jobcol.backend.shared.dto.LoginRequest;
import com.jobcol.backend.shared.dto.RegisterRequest;
import com.jobcol.backend.shared.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserService userService;
    @Mock private RestTemplate restTemplate;
    @Mock private JavaMailSender mailSender;
    @Mock private NotificationService notificationService;
    @Mock private verificationRepository verificationRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private UserDTO userDTO;
    private Jwt jwt;
    private Authentication authentication;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("santiago@test.com")
                .username("santi")
                .keycloakUserId("kc-abc123")
                .build();

        userDTO = UserDTO.builder()
                .id(1L)
                .email("santiago@test.com")
                .username("santi")
                .firstName("Santiago")
                .lastName("Gomez")
                .role("TRABAJADOR")
                .active(true)
                .build();

        jwt = mock(Jwt.class);
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(jwt);
        SecurityContextHolder.setContext(securityContext);
    }

    // ── syncUserWithDatabase ─────────────────────────────────────

    @Test
    void syncUserWithDatabase_whenUserExists_shouldReturnExistingUser() {
        when(jwt.getSubject()).thenReturn("kc-abc123");
        when(jwt.getClaim("email")).thenReturn("santiago@test.com");
        when(jwt.getClaim("preferred_username")).thenReturn("santi");
        when(jwt.getClaim("given_name")).thenReturn("Santiago");
        when(jwt.getClaim("family_name")).thenReturn("Gomez");
        when(jwt.getClaimAsMap("realm_access")).thenReturn(Map.of("roles", List.of("WORKER")));
        when(userService.getUserByEmail("santiago@test.com")).thenReturn(Optional.of(userDTO));

        UserDTO result = authService.syncUserWithDatabase();

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("santiago@test.com");
        verify(userService, never()).createUser(any());
    }

    @Test
    void syncUserWithDatabase_whenUserNotExists_shouldCreateNewUser() {
        when(jwt.getSubject()).thenReturn("kc-abc123");
        when(jwt.getClaim("email")).thenReturn("nuevo@test.com");
        when(jwt.getClaim("preferred_username")).thenReturn("nuevo");
        when(jwt.getClaim("given_name")).thenReturn("Nuevo");
        when(jwt.getClaim("family_name")).thenReturn("Usuario");
        when(jwt.getClaimAsMap("realm_access")).thenReturn(Map.of("roles", List.of("WORKER")));
        when(userService.getUserByEmail("nuevo@test.com")).thenReturn(Optional.empty());
        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        UserDTO result = authService.syncUserWithDatabase();

        assertThat(result).isNotNull();
        verify(userService).createUser(any(UserDTO.class));
    }

    // ── login ────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void login_shouldReturnTokensAndSendCode() {
        LoginRequest request = new LoginRequest("santi", "password123");

        Map<String, Object> tokenResponse = Map.of(
                "access_token", "access-token-123",
                "refresh_token", "refresh-token-456"
        );

        ResponseEntity<Map> tokenEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(tokenEntity);

        when(userRepository.findByUsername("santi")).thenReturn(Optional.of(user));

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode("123456");
        verificationCode.setUser(user);
        verificationCode.setExpiration(LocalDateTime.now().plusMinutes(5));
        verificationCode.setUsed(false);

        when(userRepository.findByEmail("santiago@test.com")).thenReturn(Optional.of(user));
        when(verificationRepository.save(any(VerificationCode.class))).thenReturn(verificationCode);
        when(notificationService.createNotification(any(), any(), any(), any())).thenReturn(null);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        Map<String, Object> result = authService.login(request);

        assertThat(result).containsKey("accessToken");
        assertThat(result).containsKey("requiresVerification");
        assertThat(result.get("requiresVerification")).isEqualTo(true);
        verify(userRepository).findByUsername("santi");
    }

    @Test
    void login_whenUserNotFound_shouldThrowException() {
        LoginRequest request = new LoginRequest("noexiste", "password");

        Map<String, Object> tokenResponse = Map.of(
                "access_token", "token",
                "refresh_token", "refresh"
        );
        ResponseEntity<Map> tokenEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(tokenEntity);

        when(userRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    // ── generateCodeByEmail ──────────────────────────────────────

    @Test
    void generateCodeByEmail_shouldSaveCodeAndSendEmail() {
        when(userRepository.findByEmail("santiago@test.com")).thenReturn(Optional.of(user));
        when(verificationRepository.save(any(VerificationCode.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(notificationService.createNotification(any(), any(), any(), any())).thenReturn(null);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        String code = authService.generateCodeByEmail("santiago@test.com");

        assertThat(code).hasSize(6);
        verify(verificationRepository).save(any(VerificationCode.class));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void generateCodeByEmail_whenUserNotFound_shouldThrowException() {
        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.generateCodeByEmail("noexiste@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    // ── verifyCodeByEmail ────────────────────────────────────────

    @Test
    void verifyCodeByEmail_whenValidCode_shouldReturnTrue() {
        VerificationCode verification = new VerificationCode();
        verification.setCode("123456");
        verification.setUser(user);
        verification.setExpiration(LocalDateTime.now().plusMinutes(5));
        verification.setUsed(false);

        when(userRepository.findByEmail("santiago@test.com")).thenReturn(Optional.of(user));
        when(verificationRepository.findTopByUserIdAndCodeAndUsedFalseOrderByExpirationDesc(1L, "123456"))
                .thenReturn(Optional.of(verification));
        when(verificationRepository.save(verification)).thenReturn(verification);

        boolean result = authService.verifyCodeByEmail("santiago@test.com", "123456");

        assertThat(result).isTrue();
        assertThat(verification.isUsed()).isTrue();
    }

    @Test
    void verifyCodeByEmail_whenExpiredCode_shouldThrowException() {
        VerificationCode verification = new VerificationCode();
        verification.setCode("123456");
        verification.setUser(user);
        verification.setExpiration(LocalDateTime.now().minusMinutes(10)); // expirado
        verification.setUsed(false);

        when(userRepository.findByEmail("santiago@test.com")).thenReturn(Optional.of(user));
        when(verificationRepository.findTopByUserIdAndCodeAndUsedFalseOrderByExpirationDesc(1L, "123456"))
                .thenReturn(Optional.of(verification));

        assertThatThrownBy(() -> authService.verifyCodeByEmail("santiago@test.com", "123456"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Código expirado");
    }

    @Test
    void verifyCodeByEmail_whenInvalidCode_shouldThrowException() {
        when(userRepository.findByEmail("santiago@test.com")).thenReturn(Optional.of(user));
        when(verificationRepository.findTopByUserIdAndCodeAndUsedFalseOrderByExpirationDesc(1L, "999999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.verifyCodeByEmail("santiago@test.com", "999999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Código inválido");
    }

    @Test
    void verifyCodeByEmail_whenUserNotFound_shouldThrowException() {
        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.verifyCodeByEmail("noexiste@test.com", "123456"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    // ── resetPassword ────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void resetPassword_whenValidCode_shouldResetAndReturnMessage() {
        VerificationCode verification = new VerificationCode();
        verification.setCode("123456");
        verification.setUser(user);
        verification.setExpiration(LocalDateTime.now().plusMinutes(5));
        verification.setUsed(false);

        when(userRepository.findByEmail("santiago@test.com")).thenReturn(Optional.of(user));
        when(verificationRepository.findTopByUserIdAndCodeAndUsedFalseOrderByExpirationDesc(1L, "123456"))
                .thenReturn(Optional.of(verification));
        when(verificationRepository.save(verification)).thenReturn(verification);

        // mock getAdminToken
        Map<String, Object> adminTokenResponse = Map.of("access_token", "admin-token");
        ResponseEntity<Map> adminTokenEntity = new ResponseEntity<>(adminTokenResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(adminTokenEntity);

        // mock reset password call
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        String result = authService.resetPassword("santiago@test.com", "123456", "nuevaPassword123");

        assertThat(result).contains("Contraseña actualizada");
        assertThat(verification.isUsed()).isTrue();
    }

    @Test
    void resetPassword_whenExpiredCode_shouldThrowException() {
        VerificationCode verification = new VerificationCode();
        verification.setCode("123456");
        verification.setUser(user);
        verification.setExpiration(LocalDateTime.now().minusMinutes(10));
        verification.setUsed(false);

        when(userRepository.findByEmail("santiago@test.com")).thenReturn(Optional.of(user));
        when(verificationRepository.findTopByUserIdAndCodeAndUsedFalseOrderByExpirationDesc(1L, "123456"))
                .thenReturn(Optional.of(verification));

        assertThatThrownBy(() -> authService.resetPassword("santiago@test.com", "123456", "nueva"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Código expirado");
    }

    @Test
    void resetPassword_whenUserNotFound_shouldThrowException() {
        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.resetPassword("noexiste@test.com", "123456", "nueva"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    // ── sendEmail ────────────────────────────────────────────────

    @Test
    void sendEmail_shouldCallMailSender() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        authService.sendEmail("santiago@test.com", "Asunto", "Cuerpo del mensaje");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}