package com.jobcol.backend.AuthService.service.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.jobcol.backend.AuthService.model.VerificationCode;
import com.jobcol.backend.AuthService.repository.verificationRepository;
import com.jobcol.backend.AuthService.service.AuthService;
import com.jobcol.backend.NotificationService.model.EmailTemplateBuilder;
import com.jobcol.backend.NotificationService.model.NotificationType;
import com.jobcol.backend.NotificationService.service.NotificationService;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.UserService.service.UserService;
import com.jobcol.backend.shared.dto.LoginRequest;
import com.jobcol.backend.shared.dto.RegisterRequest;
import com.jobcol.backend.shared.dto.UserDTO;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final UserService userService;
        private final RestTemplate restTemplate;
        private final JavaMailSender mailSender;
        private final NotificationService notificationService;
        private final verificationRepository verificationRepository;
        private final UserRepository userRepository;    

        @Value("${keycloak.url}")
        private String keycloakUrl;

        @Value("${keycloak.realm}")
        private String realm;

        @Value("${keycloak.client-id}")
        private String clientId;

        @Value("${keycloak.admin-user}")
        private String adminUser;

        @Value("${keycloak.admin-password}")
        private String adminPassword;

        @Override
        public UserDTO syncUserWithDatabase() {

        Jwt jwt = getJwt();

        String keycloakId = jwt.getSubject();
        String email = jwt.getClaim("email");
        String username = jwt.getClaim("preferred_username");
        String firstName = jwt.getClaim("given_name");
        String lastName = jwt.getClaim("family_name");

        String role = extractRole(jwt);

        Optional<UserDTO> existingUser = userService.getUserByEmail(email);

        if (existingUser.isPresent()) {
                return existingUser.get();
        }

        UserDTO newUser = UserDTO.builder()
                .keycloakUserId(keycloakId)
                .email(email)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .active(true)
                .creationDate(LocalDateTime.now())
                .build();

        return userService.createUser(newUser);
        }

        @Override
        public Map<String, Object> login(LoginRequest request) {

                String tokenUrl =
                        keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                params.add("grant_type", "password");
                params.add("client_id", clientId);
                params.add("username", request.getUsername());
                params.add("password", request.getPassword());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String, String>> entity =
                        new HttpEntity<>(params, headers);

                ResponseEntity<Map> response =
                        restTemplate.postForEntity(tokenUrl, entity, Map.class);

                Map<String, Object> body = response.getBody();

                String accessToken = (String) body.get("access_token");
                String refreshToken = (String) body.get("refresh_token");

                // Buscar usuario por username
                User user = userRepository.findByUsername(request.getUsername())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                // Generar código
                generateCodeByEmail(user.getEmail());

                return Map.of(
                        "message", "Login exitoso. Código enviado",
                        "userId", user.getId(),
                        "email", user.getEmail(),
                        "requiresVerification", true,
                        "accessToken", accessToken,
                        "refreshToken", refreshToken
                );
        }

        @Override
        public Map<String, Object> register(RegisterRequest request) {

        String adminToken = getAdminToken();

        String createUserUrl =
                keycloakUrl + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> user = Map.of(
                "username", request.getUsername(),
                "email", request.getEmail().trim().toLowerCase(),
                "enabled", true,
                "credentials", new Object[]{
                        Map.of(
                                "type", "password",
                                "value", request.getPassword(),
                                "temporary", false
                        )
                }
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(user, headers);

        String keycloakId;

        try {
                
                ResponseEntity<String> response =
                        restTemplate.postForEntity(createUserUrl, entity, String.class);

                String location = response.getHeaders().getFirst("Location");
                keycloakId = location.substring(location.lastIndexOf("/") + 1);

        } catch (HttpClientErrorException.Conflict e) {

                String searchUrl = keycloakUrl + "/admin/realms/" + realm
                        + "/users?email=" + request.getEmail().trim().toLowerCase();

                HttpEntity<Void> searchEntity = new HttpEntity<>(headers);

                ResponseEntity<Object[]> searchResponse =
                        restTemplate.exchange(searchUrl, HttpMethod.GET, searchEntity, Object[].class);

                if (searchResponse.getBody().length == 0) {
                throw new RuntimeException("Usuario existe en Keycloak pero no se pudo recuperar");
                }

                Map<String, Object> existingUser =
                        (Map<String, Object>) searchResponse.getBody()[0];

                keycloakId = (String) existingUser.get("id");
        }

        
        String role = "TRABAJADOR";
        if ("ADMIN".equals(request.getRole())) role = "ADMIN";
        if ("EMPLOYER".equals(request.getRole())) role = "EMPLEADOR";
        if ("EMPLEADOR".equals(request.getRole())) role = "EMPLEADOR";

        Optional<User> existingDbUser =
                userRepository.findByEmail(request.getEmail().trim().toLowerCase());

        if (existingDbUser.isEmpty()) {

                UserDTO newUser = UserDTO.builder()
                        .keycloakUserId(keycloakId)
                        .email(request.getEmail().trim().toLowerCase())
                        .username(request.getUsername())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .cedula(request.getCedula())
                        .role(role)
                        .active(true)
                        .creationDate(LocalDateTime.now())
                        .phone(request.getPhone())
                        .build();

                userService.createUser(newUser);
        }

        String tokenUrl =
                keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", clientId);
        params.add("username", request.getUsername());
        params.add("password", request.getPassword());

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> loginEntity =
                new HttpEntity<>(params, loginHeaders);

        ResponseEntity<Map> loginResponse =
                restTemplate.postForEntity(tokenUrl, loginEntity, Map.class);

        Map<String, Object> tokenBody = loginResponse.getBody();

        return Map.of(
                "message", "User registered successfully",
                "keycloakId", keycloakId,
                "accessToken", tokenBody.get("access_token"),
                "refreshToken", tokenBody.get("refresh_token")
        );
}

        private String getAdminToken() {
                String tokenUrl =
                        keycloakUrl + "/realms/master/protocol/openid-connect/token";

                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

                params.add("grant_type", "password");
                params.add("client_id", "admin-cli");
                params.add("username", adminUser);
                params.add("password", adminPassword);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String, String>> entity =
                        new HttpEntity<>(params, headers);

                ResponseEntity<Map> response =
                        restTemplate.postForEntity(tokenUrl, entity, Map.class);

                return (String) response.getBody().get("access_token");
        }

        private String extractRole(Jwt jwt) {
                var realmAccess = jwt.getClaimAsMap("realm_access");

                if (realmAccess == null) return "TRABAJADOR";

                Object rolesObject = realmAccess.get("roles");

                if (rolesObject instanceof java.util.List<?> roleList) {

                for (Object role : roleList) {

                        if ("ADMIN".equals(role)) return "ADMIN";
                        if ("EMPLOYER".equals(role)) return "EMPLEADOR";
                        if ("WORKER".equals(role)) return "TRABAJADOR";
                }
                }
                return "TRABAJADOR";
        }

        private Jwt getJwt() {
                Authentication authentication =
                        SecurityContextHolder.getContext().getAuthentication();

                return (Jwt) authentication.getPrincipal();
        }

        @Async
        @Override
        public void sendEmail(String to, String subject, String body) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);

                mailSender.send(message);
        }

        @Override
        public String generateCodeByEmail(String email) {

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String code = String.valueOf((int)(Math.random() * 900000) + 100000);

        VerificationCode verification = new VerificationCode();
        verification.setCode(code);
        verification.setUser(user);
        verification.setExpiration(LocalDateTime.now().plusMinutes(5));
        verification.setUsed(false);

        verificationRepository.save(verification);

        notificationService.createNotificationOnly(
                user.getId(),
                "Código de verificación",
                "Se envió un código de verificación a tu correo electrónico.",
                NotificationType.SEGURIDAD
        );

        String html = EmailTemplateBuilder.buildVerificationCodeEmail(
                user.getUsername(),
                code
        );

        notificationService.sendEmail(
                user.getEmail(),
                "Código de verificación - JobCol",
                html
        );

        return code;
        }

        @Override
        public boolean verifyCodeByEmail(String email, String code) {

        User user = userRepository.findByEmail(email.trim().toLowerCase())
        .orElseThrow(() -> new RuntimeException(
                "Usuario no encontrado con email: " + email
        ));

        VerificationCode verification = verificationRepository
                .findTopByUserIdAndCodeAndUsedFalseOrderByExpirationDesc(user.getId(), code)
                .orElseThrow(() -> new RuntimeException("Código inválido"));

        if (verification.getExpiration().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Código expirado");
        }

        verification.setUsed(true);
        verificationRepository.save(verification);

        return true;
        }

        @Override
        public String resetPassword(String email, String code, String newPassword) {

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        VerificationCode verification = verificationRepository
                .findTopByUserIdAndCodeAndUsedFalseOrderByExpirationDesc(user.getId(), code)
                .orElseThrow(() -> new RuntimeException("Código inválido"));

        if (verification.getExpiration().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Código expirado");
        }

        verification.setUsed(true);
        verificationRepository.save(verification);

        String adminToken = getAdminToken();

        String resetPasswordUrl = keycloakUrl
                + "/admin/realms/" + realm
                + "/users/" + user.getKeycloakUserId()
                + "/reset-password";

        Map<String, Object> passwordData = Map.of(
                "type", "password",
                "value", newPassword,
                "temporary", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(passwordData, headers);

        restTemplate.exchange(
                resetPasswordUrl,
                HttpMethod.PUT,
                entity,
                Void.class
        );

        return "Contraseña actualizada correctamente";
        }
}