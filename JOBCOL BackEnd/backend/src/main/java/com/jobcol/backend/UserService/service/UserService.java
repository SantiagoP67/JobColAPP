package com.jobcol.backend.UserService.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.jobcol.backend.shared.dto.UserDTO;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(Long id, UserDTO userDTO);

    Optional<UserDTO> getUserById(Long id);

    Optional<UserDTO> getUserByEmail(String email);

    List<UserDTO> getAllUsers();

    void deleteUser(Long id);

    void deactivateUser(Long id);

    Optional<UserDTO> getUserByKeycloakUserId(String keycloakUserId);

    UserDTO updatePhoto(Long userId, MultipartFile file);
    
} 
