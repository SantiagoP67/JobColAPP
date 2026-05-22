package com.jobcol.backend.UserService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jobcol.backend.UserService.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByKeycloakUserId(String keycloakUserId);

    Optional<User> findByUsername(String username);

    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);

    List<User> findByRole(String role);
    
}
