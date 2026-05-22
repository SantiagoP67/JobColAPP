package com.jobcol.backend.UserService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobcol.backend.UserService.model.Profile;
import com.jobcol.backend.UserService.model.User;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserId(Long userId);

    boolean existsByUser(User user);

    Optional<Profile> findByUser_Id(Long userId);
    
}
