package com.jobcol.backend.NotificationService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.jobcol.backend.NotificationService.model.NotificationType;
import com.jobcol.backend.NotificationService.service.NotificationService;
import com.jobcol.backend.shared.dto.NotificationDTO;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/create")
    public ResponseEntity<NotificationDTO> createNotification(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam NotificationType type
    ) {
        return ResponseEntity.ok(
                notificationService.createNotification(userId, title, message, type)
        );
    }

    @PostMapping("/batch")
    public ResponseEntity<String> createNotificationsBatch(
            @RequestBody BatchNotificationRequest request
    ) {

        notificationService.createNotificationsBatch(
                request.getUserIds(),
                request.getTitle(),
                request.getMessage(),
                request.getType()
        );

        return ResponseEntity.ok("Notifications sent successfully");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                notificationService.getUserNotifications(userId)
        );
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                notificationService.getUnreadNotifications(userId)
        );
    }

    @GetMapping("/user/{userId}/count-unread")
    public ResponseEntity<Long> countUnread(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                notificationService.countUnread(userId)
        );
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                notificationService.markAsRead(id)
        );
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<String> markAllAsRead(
            @PathVariable Long userId
    ) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok("All notifications marked as read");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(
            @PathVariable Long id
    ) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Notification deleted");
    }
}