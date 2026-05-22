package com.jobcol.backend.NotificationService.controller;

import lombok.Data;
import java.util.List;
import com.jobcol.backend.NotificationService.model.NotificationType;

@Data
public class BatchNotificationRequest {
    private List<Long> userIds;
    private String title;
    private String message;
    private NotificationType type;
}
