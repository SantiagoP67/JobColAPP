package com.jobcol.backend.AuthService.controller;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jobcol.backend.AuthService.service.AuthService;
import com.jobcol.backend.shared.dto.LoginRequest;
import com.jobcol.backend.shared.dto.RegisterRequest;
import com.jobcol.backend.shared.dto.UserDTO;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService; 

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO user = authService.syncUserWithDatabase();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {

        String email = body.get("email");

        authService.generateCodeByEmail(email);

        return ResponseEntity.ok(Map.of(
                "message", "Código enviado al correo"
        ));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String code = body.get("code");

        boolean valid = authService.verifyCodeByEmail(email, code);

        return ResponseEntity.ok(Map.of(
                "valid", valid,
                "message", "Código válido"
        ));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String code = body.get("code");
        String newPassword = body.get("newPassword");

        String response = authService.resetPassword(email, code, newPassword);

        return ResponseEntity.ok(Map.of("message", response));
    }
}