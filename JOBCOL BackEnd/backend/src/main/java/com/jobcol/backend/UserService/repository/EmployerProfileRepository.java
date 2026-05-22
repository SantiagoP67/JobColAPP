package com.jobcol.backend.UserService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobcol.backend.UserService.model.EmployerProfile;
import com.jobcol.backend.UserService.model.User;

public interface EmployerProfileRepository extends JpaRepository<EmployerProfile, Long> {
    Optional<EmployerProfile> findByUserId(Long userId);

    boolean existsByUser(User user);

    Optional<EmployerProfile> findByUser_Id(Long userId);
}
