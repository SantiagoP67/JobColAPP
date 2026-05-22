package com.jobcol.backend.AuthService.service;

import java.util.Map;

import com.jobcol.backend.shared.dto.LoginRequest;
import com.jobcol.backend.shared.dto.RegisterRequest;
import com.jobcol.backend.shared.dto.UserDTO;

public interface AuthService {
    UserDTO syncUserWithDatabase();
    Map<String, Object> register(RegisterRequest request);
    Map<String, Object> login(LoginRequest request);
    void sendEmail(String to, String subject, String body);
    String generateCodeByEmail(String email);
    boolean verifyCodeByEmail(String email, String code);
    String resetPassword(String email, String code, String newPassword);
}
