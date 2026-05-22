package com.jobcol.backend.shared.mappers;


import com.jobcol.backend.NotificationService.model.Notification;
import com.jobcol.backend.shared.dto.NotificationDTO;

public class NotificationMapper {

    // Entity → DTO
    public static NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        Long userId = notification.getUser() != null
                ? notification.getUser().getId()
                : null;

        return new NotificationDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getRead(),
                notification.getCreatedAt(),
                userId
        );
    }

    // DTO → Entity
    public static Notification toEntity(NotificationDTO dto) {
        if (dto == null) {
            return null;
        }

        return Notification.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .message(dto.getMessage())
                .type(dto.getType())
                .read(dto.getRead())
                .createdAt(dto.getCreatedAt())
                // user se asigna en el service
                .build();
    }
}
