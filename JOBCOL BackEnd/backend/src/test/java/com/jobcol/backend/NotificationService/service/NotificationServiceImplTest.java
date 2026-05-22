package com.jobcol.backend.NotificationService.service;

import com.jobcol.backend.NotificationService.model.Notification;
import com.jobcol.backend.NotificationService.model.NotificationType;
import com.jobcol.backend.NotificationService.repository.NotificationRepository;
import com.jobcol.backend.NotificationService.service.impl.NotificationServiceImpl;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.shared.dto.NotificationDTO;
import com.jobcol.backend.shared.mappers.NotificationMapper;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private UserRepository userRepository;
    @Mock private JavaMailSender mailSender;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Notification notification;
    private NotificationDTO notificationDTO;
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("santiago@test.com")
                .username("santi")
                .build();

        notification = Notification.builder()
                .id(1L)
                .title("Título test")
                .message("Mensaje test")
                .type(NotificationType.SEGURIDAD)
                .read(false)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        notificationDTO = NotificationDTO.builder()
                .id(1L)
                .title("Título test")
                .message("Mensaje test")
                .type(NotificationType.SEGURIDAD)
                .read(false)
                .createdAt(LocalDateTime.now())
                .userId(1L)
                .build();

        // MimeMessage necesita ser mockeado para que mailSender.createMimeMessage() no falle
        mimeMessage = mock(MimeMessage.class);
        lenient().when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    // ── createNotification ───────────────────────────────────────

    @Test
    void createNotification_shouldCreateAndReturnDTO() {
        try (MockedStatic<NotificationMapper> mapper = mockStatic(NotificationMapper.class)) {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
            mapper.when(() -> NotificationMapper.toDTO(notification)).thenReturn(notificationDTO);
            doNothing().when(mailSender).send(any(MimeMessage.class));

            NotificationDTO result = notificationService.createNotification(
                    1L, "Título test", "Mensaje test", NotificationType.SEGURIDAD
            );

            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Título test");
            verify(notificationRepository).save(any(Notification.class));
            verify(mailSender).send(any(MimeMessage.class));
        }
    }

    @Test
    void createNotification_whenUserNotFound_shouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.createNotification(
                99L, "Título", "Mensaje", NotificationType.SEGURIDAD
        ))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void createNotification_whenUserHasNoEmail_shouldNotSendEmail() {
        try (MockedStatic<NotificationMapper> mapper = mockStatic(NotificationMapper.class)) {
            User userSinEmail = User.builder().id(2L).email(null).build();
            when(userRepository.findById(2L)).thenReturn(Optional.of(userSinEmail));
            when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
            mapper.when(() -> NotificationMapper.toDTO(notification)).thenReturn(notificationDTO);

            notificationService.createNotification(
                    2L, "Título", "Mensaje", NotificationType.SEGURIDAD
            );

            verify(mailSender, never()).send(any(MimeMessage.class));
        }
    }

    // ── getById ──────────────────────────────────────────────────

    @Test
    void getById_whenExists_shouldReturnDTO() {
        try (MockedStatic<NotificationMapper> mapper = mockStatic(NotificationMapper.class)) {
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
            mapper.when(() -> NotificationMapper.toDTO(notification)).thenReturn(notificationDTO);

            Optional<NotificationDTO> result = notificationService.getById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
        }
    }

    @Test
    void getById_whenNotExists_shouldReturnEmpty() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<NotificationDTO> result = notificationService.getById(99L);

        assertThat(result).isEmpty();
    }

    // ── getUserNotifications ─────────────────────────────────────

    @Test
    void getUserNotifications_shouldReturnList() {
        try (MockedStatic<NotificationMapper> mapper = mockStatic(NotificationMapper.class)) {
            when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
                    .thenReturn(List.of(notification));
            mapper.when(() -> NotificationMapper.toDTO(notification)).thenReturn(notificationDTO);

            List<NotificationDTO> result = notificationService.getUserNotifications(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Título test");
        }
    }

    @Test
    void getUserNotifications_whenEmpty_shouldReturnEmptyList() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(99L))
                .thenReturn(List.of());

        List<NotificationDTO> result = notificationService.getUserNotifications(99L);

        assertThat(result).isEmpty();
    }

    // ── getUnreadNotifications ───────────────────────────────────

    @Test
    void getUnreadNotifications_shouldReturnUnreadList() {
        try (MockedStatic<NotificationMapper> mapper = mockStatic(NotificationMapper.class)) {
            when(notificationRepository.findByUserIdAndReadFalse(1L))
                    .thenReturn(List.of(notification));
            mapper.when(() -> NotificationMapper.toDTO(notification)).thenReturn(notificationDTO);

            List<NotificationDTO> result = notificationService.getUnreadNotifications(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getRead()).isFalse();
        }
    }

    @Test
    void getUnreadNotifications_whenEmpty_shouldReturnEmptyList() {
        when(notificationRepository.findByUserIdAndReadFalse(99L)).thenReturn(List.of());

        List<NotificationDTO> result = notificationService.getUnreadNotifications(99L);

        assertThat(result).isEmpty();
    }

    // ── countUnread ──────────────────────────────────────────────

    @Test
    void countUnread_shouldReturnCount() {
        when(notificationRepository.countByUserIdAndReadFalse(1L)).thenReturn(3L);

        Long count = notificationService.countUnread(1L);

        assertThat(count).isEqualTo(3L);
    }

    @Test
    void countUnread_whenNone_shouldReturnZero() {
        when(notificationRepository.countByUserIdAndReadFalse(1L)).thenReturn(0L);

        Long count = notificationService.countUnread(1L);

        assertThat(count).isZero();
    }

    // ── markAsRead ───────────────────────────────────────────────

    @Test
    void markAsRead_shouldSetReadTrueAndReturnDTO() {
        try (MockedStatic<NotificationMapper> mapper = mockStatic(NotificationMapper.class)) {
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
            when(notificationRepository.save(notification)).thenReturn(notification);
            mapper.when(() -> NotificationMapper.toDTO(notification)).thenReturn(notificationDTO);

            NotificationDTO result = notificationService.markAsRead(1L);

            assertThat(notification.getRead()).isTrue();
            assertThat(result).isNotNull();
            verify(notificationRepository).save(notification);
        }
    }

    @Test
    void markAsRead_whenNotFound_shouldThrowException() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Notification not found");
    }

    // ── markAllAsRead ────────────────────────────────────────────

    @Test
    void markAllAsRead_shouldMarkAllAndSave() {
        when(notificationRepository.findByUserIdAndReadFalse(1L))
                .thenReturn(List.of(notification));
        when(notificationRepository.saveAll(anyList())).thenReturn(List.of(notification));

        notificationService.markAllAsRead(1L);

        assertThat(notification.getRead()).isTrue();
        verify(notificationRepository).saveAll(anyList());
    }

    @Test
    void markAllAsRead_whenNoUnread_shouldSaveEmptyList() {
        when(notificationRepository.findByUserIdAndReadFalse(1L)).thenReturn(List.of());

        notificationService.markAllAsRead(1L);

        verify(notificationRepository).saveAll(List.of());
    }

    // ── deleteNotification ───────────────────────────────────────

    @Test
    void deleteNotification_shouldCallDeleteById() {
        doNothing().when(notificationRepository).deleteById(1L);

        notificationService.deleteNotification(1L);

        verify(notificationRepository).deleteById(1L);
    }

    // ── sendEmail ────────────────────────────────────────────────

    @Test
    void sendEmail_shouldCallMailSender() {
        doNothing().when(mailSender).send(any(MimeMessage.class));

        notificationService.sendEmail("santiago@test.com", "Asunto", "<p>Cuerpo</p>");

        verify(mailSender).send(any(MimeMessage.class));
    }
}