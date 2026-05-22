package com.jobcol.backend.UserService.controller;

import java.security.Principal;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobcol.backend.UserService.service.EmployerProfileService;
import com.jobcol.backend.shared.dto.EmployerProfileDTO;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/employer-profiles")
@RequiredArgsConstructor
public class EmployerProfileController {
     private final EmployerProfileService employerProfileService;

    @PostMapping
    public ResponseEntity<EmployerProfileDTO> createProfile( @RequestBody EmployerProfileDTO dto) {
       EmployerProfileDTO createdProfileDTO = employerProfileService.createProfile(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProfileDTO);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<EmployerProfileDTO> getProfile(
            @PathVariable Long userId
    ) {
        EmployerProfileDTO profile = employerProfileService.getByUserId(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<EmployerProfileDTO> updateProfile(
            @PathVariable Long userId,
            @RequestBody EmployerProfileDTO dto
    ) {
        EmployerProfileDTO updated = employerProfileService.updateProfile(userId, dto);
        return ResponseEntity.ok(updated);
    }
}
