package com.jobcol.backend.AuthService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobcol.backend.AuthService.model.VerificationCode;
import com.jobcol.backend.shared.dto.UserDTO;

public interface verificationRepository extends JpaRepository<VerificationCode, Long> {
    VerificationCode findByCode(String code);

    Optional<VerificationCode> findTopByUserIdAndCodeAndUsedFalseOrderByExpirationDesc(
            Long userId,
            String code
    );
    
}
