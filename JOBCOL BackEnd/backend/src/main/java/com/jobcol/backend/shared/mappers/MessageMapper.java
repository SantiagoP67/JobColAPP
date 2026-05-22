package com.jobcol.backend.shared.mappers;

import com.jobcol.backend.MessagingService.model.Message;
import com.jobcol.backend.shared.dto.MessageDTO;

public class MessageMapper {

    // Entity → DTO
    public static MessageDTO toDTO(Message message) {
        if (message == null) {
            return null;
        }

        Long senderId = message.getSender() != null
                ? message.getSender().getId()
                : null;

        Long receiverId = message.getReceiver() != null
                ? message.getReceiver().getId()
                : null;

        return new MessageDTO(
                message.getId(),
                message.getContent(),
                message.getSentDate(),
                message.getRead(),
                senderId,
                receiverId
        );
    }

    // DTO → Entity
    public static Message toEntity(MessageDTO dto) {
        if (dto == null) {
            return null;
        }

        return Message.builder()
                .id(dto.getId())
                .content(dto.getContent())
                .sentDate(dto.getSentDate())
                .read(dto.getRead())
                // sender y receiver se asignan en el service
                .build();
    }
}
