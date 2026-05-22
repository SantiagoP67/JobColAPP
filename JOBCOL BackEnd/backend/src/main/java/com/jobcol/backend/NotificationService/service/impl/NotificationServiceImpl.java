package com.jobcol.backend.NotificationService.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.jobcol.backend.NotificationService.model.Notification;
import com.jobcol.backend.NotificationService.model.NotificationType;
import com.jobcol.backend.NotificationService.repository.NotificationRepository;
import com.jobcol.backend.NotificationService.service.NotificationService;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.shared.dto.NotificationDTO;
import com.jobcol.backend.shared.mappers.NotificationMapper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

        private final NotificationRepository notificationRepository;
        private final UserRepository userRepository;
        private final JavaMailSender mailSender;

        @Override
        public NotificationDTO createNotification(
                Long userId,
                String title,
                String message,
                NotificationType type
        ) {

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                Notification notification = Notification.builder()
                        .title(title)
                        .message(message)
                        .type(type)
                        .read(false)
                        .createdAt(LocalDateTime.now())
                        .user(user)
                        .build();

                return NotificationMapper.toDTO(
                        notificationRepository.save(notification)
                );
        }

        @Async
        @Override
        public void createNotificationsBatch(
                List<Long> userIds,
                String title,
                String message,
                NotificationType type
        ) {

                List<User> users = userRepository.findAllById(userIds);

                List<Notification> notifications = users.stream()
                        .map(user -> Notification.builder()
                                .title(title)
                                .message(message)
                                .type(type)
                                .read(false)
                                .createdAt(LocalDateTime.now())
                                .user(user)
                                .build())
                        .collect(Collectors.toList());

                notificationRepository.saveAll(notifications);
        }

        @Override
        public Optional<NotificationDTO> getById(Long id) {
                return notificationRepository.findById(id)
                        .map(NotificationMapper::toDTO);
        }

        @Override
        public List<NotificationDTO> getUserNotifications(Long userId) {
                return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                        .stream()
                        .map(NotificationMapper::toDTO)
                        .collect(Collectors.toList());
        }

        @Override
        public List<NotificationDTO> getUnreadNotifications(Long userId) {
                return notificationRepository.findByUserIdAndReadFalse(userId)
                        .stream()
                        .map(NotificationMapper::toDTO)
                        .collect(Collectors.toList());
        }

        @Override
        public Long countUnread(Long userId) {
                return notificationRepository.countByUserIdAndReadFalse(userId);
        }

        @Override
        public NotificationDTO markAsRead(Long id) {

                Notification notification = notificationRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Notification not found"));

                notification.setRead(true);

                return NotificationMapper.toDTO(
                        notificationRepository.save(notification)
                );
        }

        @Override
        public void markAllAsRead(Long userId) {

                List<Notification> notifications =
                        notificationRepository.findByUserIdAndReadFalse(userId);

                notifications.forEach(n -> n.setRead(true));

                notificationRepository.saveAll(notifications);
        }

        @Override
        public void deleteNotification(Long id) {
                notificationRepository.deleteById(id);
        }

        @Async
        @Override
        public void sendEmail(
                String to,
                String subject,
                String body
        ) {

                try {

                MimeMessage message = mailSender.createMimeMessage();

                MimeMessageHelper helper =
                        new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(to);
                helper.setSubject(subject);

                // true = HTML
                helper.setText(body, true);

                mailSender.send(message);

                } catch (MessagingException e) {
                throw new RuntimeException("Error sending email", e);
                }
        }

        @Override
        public NotificationDTO createNotificationOnly(
                Long userId,
                String title,
                String message,
                NotificationType type) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(type)
                .read(false)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        return NotificationMapper.toDTO(
                notificationRepository.save(notification)
        );
        }
}