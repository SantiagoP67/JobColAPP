package com.jobcol.backend.MessagingService.service;

import java.util.List;
import java.util.Optional;

import com.jobcol.backend.shared.dto.MessageDTO;

public interface MessagingService {

    MessageDTO sendMessage(MessageDTO messageDTO);

    Optional<MessageDTO> getMessageById(Long id);

    List<MessageDTO> getInbox(Long userId);

    List<MessageDTO> getSentMessages(Long userId);

    List<MessageDTO> getConversation(Long user1Id, Long user2Id);

    Long countUnread(Long userId);

    MessageDTO markAsRead(Long messageId);

    void deleteMessage(Long id);

    String generateWhatsAppLink(Long receiverId, String message);
}
