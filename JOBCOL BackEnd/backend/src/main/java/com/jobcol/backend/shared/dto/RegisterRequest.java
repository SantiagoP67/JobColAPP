package com.jobcol.backend.shared.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String cedula;
    private String phone;
    private String role;
}
