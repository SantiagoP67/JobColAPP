package com.jobcol.backend.NotificationService.service;

import java.util.List;
import java.util.Optional;

import com.jobcol.backend.NotificationService.model.NotificationType;
import com.jobcol.backend.shared.dto.NotificationDTO;

public interface NotificationService {

    NotificationDTO createNotification(
            Long userId,
            String title,
            String message,
            NotificationType type
    );

    Optional<NotificationDTO> getById(Long id);

    List<NotificationDTO> getUserNotifications(Long userId);

    List<NotificationDTO> getUnreadNotifications(Long userId);

    Long countUnread(Long userId);

    NotificationDTO markAsRead(Long id);

    void markAllAsRead(Long userId);

    void deleteNotification(Long id);

    void createNotificationsBatch(List<Long> userIds,String title,String message,NotificationType type);
    
    void sendEmail(String to, String subject, String body);
    
    NotificationDTO createNotificationOnly(
            Long userId,
            String title,
            String message,
            NotificationType type
    );
} 