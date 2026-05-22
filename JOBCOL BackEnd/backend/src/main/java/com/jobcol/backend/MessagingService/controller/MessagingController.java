package com.jobcol.backend.MessagingService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.jobcol.backend.MessagingService.service.MessagingService;
import com.jobcol.backend.shared.dto.MessageDTO;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessagingController {

    private final MessagingService messagingService;

    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO messageDTO) {
        return ResponseEntity.ok(messagingService.sendMessage(messageDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
        return messagingService.getMessageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/inbox/{userId}")
    public ResponseEntity<List<MessageDTO>> getInbox(@PathVariable Long userId) {
        return ResponseEntity.ok(messagingService.getInbox(userId));
    }

    @GetMapping("/sent/{userId}")
    public ResponseEntity<List<MessageDTO>> getSentMessages(@PathVariable Long userId) {
        return ResponseEntity.ok(messagingService.getSentMessages(userId));
    }

    @GetMapping("/conversation")
    public ResponseEntity<List<MessageDTO>> getConversation(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id
    ) {
        return ResponseEntity.ok(
                messagingService.getConversation(user1Id, user2Id)
        );
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<Long> countUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(messagingService.countUnread(userId));
    }

    @PutMapping("/read/{messageId}")
    public ResponseEntity<MessageDTO> markAsRead(@PathVariable Long messageId) {
        return ResponseEntity.ok(messagingService.markAsRead(messageId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messagingService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/whatsapp-link/{receiverId}")
    public String getWhatsAppLink(@PathVariable Long receiverId,
                                @RequestParam String message) {
        return messagingService.generateWhatsAppLink(receiverId, message);
    }
}
