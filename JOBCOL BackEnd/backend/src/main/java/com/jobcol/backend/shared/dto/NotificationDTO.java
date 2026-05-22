package com.jobcol.backend.shared.dto;

import java.time.LocalDateTime;

import com.jobcol.backend.NotificationService.model.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO{
        private Long id;
        private String title;
        private String message;
        private NotificationType type;
        private Boolean read;
        private LocalDateTime createdAt;
        private Long userId;
}
