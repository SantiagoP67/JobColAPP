package com.jobcol.backend.UserService.service;

import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.UserService.service.impl.UserServiceImpl;
import com.jobcol.backend.shared.dto.UserDTO;
import com.jobcol.backend.shared.mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("santiago@test.com")
                .username("santi")
                .firstName("Santiago")
                .lastName("Gomez")
                .cedula("123456789")
                .role("TRABAJADOR")
                .active(true)
                .build();

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("santiago@test.com");
        userDTO.setUsername("santi");
        userDTO.setFirstName("Santiago");
        userDTO.setLastName("Gomez");
        userDTO.setCedula("123456789");
        userDTO.setRole("TRABAJADOR");
    }

    // ── createUser ──────────────────────────────────────────────

    @Test
    void createUser_shouldReturnSavedUserDTO() {
        try (MockedStatic<UserMapper> mapper = mockStatic(UserMapper.class)) {
            mapper.when(() -> UserMapper.toEntity(userDTO)).thenReturn(user);
            mapper.when(() -> UserMapper.toDTO(user)).thenReturn(userDTO);
            when(userRepository.save(user)).thenReturn(user);

            UserDTO result = userService.createUser(userDTO);

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("santiago@test.com");
            verify(userRepository).save(user);
        }
    }

    // ── updateUser ──────────────────────────────────────────────

    @Test
    void updateUser_shouldUpdateAndReturnDTO() {
        try (MockedStatic<UserMapper> mapper = mockStatic(UserMapper.class)) {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);
            mapper.when(() -> UserMapper.toDTO(user)).thenReturn(userDTO);

            UserDTO result = userService.updateUser(1L, userDTO);

            assertThat(result).isNotNull();
            verify(userRepository).save(user);
        }
    }

    @Test
    void updateUser_whenUserNotFound_shouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(99L, userDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // ── getUserById ─────────────────────────────────────────────

    @Test
    void getUserById_whenExists_shouldReturnDTO() {
        try (MockedStatic<UserMapper> mapper = mockStatic(UserMapper.class)) {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            mapper.when(() -> UserMapper.toDTO(user)).thenReturn(userDTO);

            Optional<UserDTO> result = userService.getUserById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
        }
    }

    @Test
    void getUserById_whenNotExists_shouldReturnEmpty() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.getUserById(99L);

        assertThat(result).isEmpty();
    }

    // ── getUserByEmail ──────────────────────────────────────────

    @Test
    void getUserByEmail_whenExists_shouldReturnDTO() {
        try (MockedStatic<UserMapper> mapper = mockStatic(UserMapper.class)) {
            when(userRepository.findByEmail("santiago@test.com")).thenReturn(Optional.of(user));
            mapper.when(() -> UserMapper.toDTO(user)).thenReturn(userDTO);

            Optional<UserDTO> result = userService.getUserByEmail("santiago@test.com");

            assertThat(result).isPresent();
        }
    }

    @Test
    void getUserByEmail_whenNotExists_shouldReturnEmpty() {
        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.getUserByEmail("noexiste@test.com");

        assertThat(result).isEmpty();
    }

    // ── getAllUsers ─────────────────────────────────────────────

    @Test
    void getAllUsers_shouldReturnListOfDTOs() {
        try (MockedStatic<UserMapper> mapper = mockStatic(UserMapper.class)) {
            when(userRepository.findAll()).thenReturn(List.of(user));
            mapper.when(() -> UserMapper.toDTO(user)).thenReturn(userDTO);

            List<UserDTO> result = userService.getAllUsers();

            assertThat(result).hasSize(1);
        }
    }

    @Test
    void getAllUsers_whenEmpty_shouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDTO> result = userService.getAllUsers();

        assertThat(result).isEmpty();
    }

    // ── deleteUser ──────────────────────────────────────────────

    @Test
    void deleteUser_shouldCallDeleteById() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    // ── deactivateUser ──────────────────────────────────────────

    @Test
    void deactivateUser_shouldSetActiveFalse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.deactivateUser(1L);

        assertThat(user.getActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    void deactivateUser_whenNotFound_shouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deactivateUser(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // ── getUserByKeycloakUserId ─────────────────────────────────

    @Test
    void getUserByKeycloakUserId_whenExists_shouldReturnDTO() {
        try (MockedStatic<UserMapper> mapper = mockStatic(UserMapper.class)) {
            when(userRepository.findByKeycloakUserId("kc-abc123")).thenReturn(Optional.of(user));
            mapper.when(() -> UserMapper.toDTO(user)).thenReturn(userDTO);

            Optional<UserDTO> result = userService.getUserByKeycloakUserId("kc-abc123");

            assertThat(result).isPresent();
        }
    }

    // ── updatePhoto ─────────────────────────────────────────────

    @Test
    void updatePhoto_shouldSaveFileAndUpdateImgUrl() {
        try (MockedStatic<UserMapper> mapper = mockStatic(UserMapper.class)) {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);
            mapper.when(() -> UserMapper.toDTO(user)).thenReturn(userDTO);

            MockMultipartFile file = new MockMultipartFile(
                    "file", "foto.jpg", "image/jpeg", "contenido".getBytes()
            );

            UserDTO result = userService.updatePhoto(1L, file);

            assertThat(user.getImgUrl()).contains("http://localhost:8080/uploads/");
            assertThat(user.getImgUrl()).contains("foto.jpg");
            assertThat(result).isNotNull();
        }
    }

    @Test
    void updatePhoto_whenUserNotFound_shouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        MockMultipartFile file = new MockMultipartFile(
                "file", "foto.jpg", "image/jpeg", "data".getBytes()
        );

        assertThatThrownBy(() -> userService.updatePhoto(99L, file))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }
}