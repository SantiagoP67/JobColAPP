package com.jobcol.backend.AuthService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcol.backend.AuthService.service.AuthService;
import com.jobcol.backend.shared.dto.LoginRequest;
import com.jobcol.backend.shared.dto.RegisterRequest;
import com.jobcol.backend.shared.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = UserDTO.builder()
                .id(1L)
                .email("santiago@test.com")
                .username("santi")
                .firstName("Santiago")
                .lastName("Gomez")
                .role("TRABAJADOR")
                .active(true)
                .build();
    }

    // ── GET /auth/me ─────────────────────────────────────────────

    @Test
    void getCurrentUser_shouldReturn200() throws Exception {
        when(authService.syncUserWithDatabase()).thenReturn(userDTO);

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("santiago@test.com"));
    }

    // ── POST /auth/login ─────────────────────────────────────────

    @Test
    void login_shouldReturn200WithTokens() throws Exception {
        LoginRequest request = new LoginRequest("santi", "password123");

        Map<String, Object> response = Map.of(
                "message", "Login exitoso. Código enviado",
                "accessToken", "access-token-123",
                "refreshToken", "refresh-token-456",
                "requiresVerification", true
        );

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requiresVerification").value(true));
    }

    // ── POST /auth/register ──────────────────────────────────────

    @Test
    void register_shouldReturn200() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("nuevo@test.com")
                .username("nuevo")
                .password("password123")
                .firstName("Nuevo")
                .lastName("Usuario")
                .cedula("987654321")
                .role("TRABAJADOR")
                .build();

        Map<String, Object> response = Map.of(
                "message", "User registered successfully",
                "keycloakId", "kc-xyz",
                "accessToken", "token-abc"
        );

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    // ── POST /auth/forgot-password ───────────────────────────────

    @Test
    void forgotPassword_shouldReturn200() throws Exception {
        when(authService.generateCodeByEmail("santiago@test.com")).thenReturn("123456");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"santiago@test.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Código enviado al correo"));
    }

    // ── POST /auth/verify-code ───────────────────────────────────

    @Test
    void verifyCode_whenValid_shouldReturn200() throws Exception {
        when(authService.verifyCodeByEmail("santiago@test.com", "123456")).thenReturn(true);

        mockMvc.perform(post("/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"santiago@test.com\",\"code\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    // ── POST /auth/reset-password ────────────────────────────────

    @Test
    void resetPassword_shouldReturn200() throws Exception {
        when(authService.resetPassword("santiago@test.com", "123456", "nuevaPass"))
                .thenReturn("Contraseña actualizada correctamente");

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"santiago@test.com\",\"code\":\"123456\",\"newPassword\":\"nuevaPass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Contraseña actualizada correctamente"));
    }
}