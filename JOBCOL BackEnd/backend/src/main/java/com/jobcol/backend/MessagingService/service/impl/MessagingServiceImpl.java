package com.jobcol.backend.MessagingService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.jobcol.backend.MessagingService.model.Message;
import com.jobcol.backend.MessagingService.repository.MessageRepository;
import com.jobcol.backend.MessagingService.service.MessagingService;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.shared.dto.MessageDTO;
import com.jobcol.backend.shared.mappers.MessageMapper;

@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {

        private final MessageRepository messageRepository;
        private final UserRepository userRepository;

        @Override
        public MessageDTO sendMessage(MessageDTO messageDTO) {

                User sender = userRepository.findById(messageDTO.getSenderId())
                        .orElseThrow(() -> new RuntimeException("Sender not found"));

                User receiver = userRepository.findById(messageDTO.getReceiverId())
                        .orElseThrow(() -> new RuntimeException("Receiver not found"));

                Message message = Message.builder()
                        .content(messageDTO.getContent())
                        .sentDate(LocalDateTime.now())
                        .read(false)
                        .sender(sender)
                        .receiver(receiver)
                        .build();

                return MessageMapper.toDTO(
                        messageRepository.save(message)
                );
        }

        @Override
        public Optional<MessageDTO> getMessageById(Long id) {
                return messageRepository.findById(id)
                        .map(MessageMapper::toDTO);
        }

        @Override
        public List<MessageDTO> getInbox(Long userId) {
                return messageRepository.findByReceiverIdOrderBySentDateDesc(userId)
                        .stream()
                        .map(MessageMapper::toDTO)
                        .collect(Collectors.toList());
        }

        @Override
        public List<MessageDTO> getSentMessages(Long userId) {
                return messageRepository.findByReceiverIdOrderBySentDateDesc(userId)
                        .stream()
                        .map(MessageMapper::toDTO)
                        .collect(Collectors.toList());
        }

        @Override
        public List<MessageDTO> getConversation(Long user1Id, Long user2Id) {
                return messageRepository
                        .findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderBySentDateAsc(
                                user1Id, user2Id,
                                user2Id, user1Id
                        )
                        .stream()
                        .map(MessageMapper::toDTO)
                        .collect(Collectors.toList());
        }

        @Override
        public Long countUnread(Long userId) {
                return messageRepository.countByReceiverIdAndReadFalse(userId);
        }

        @Override
        public MessageDTO markAsRead(Long messageId) {

                Message message = messageRepository.findById(messageId)
                        .orElseThrow(() -> new RuntimeException("Message not found"));

                message.setRead(true);

                return MessageMapper.toDTO(
                        messageRepository.save(message)
                );
        }

        @Override
        public void deleteMessage(Long id) {
                messageRepository.deleteById(id);
        }

        @Override
        public String generateWhatsAppLink(Long receiverId, String message) {

                User receiver = userRepository.findById(receiverId)
                        .orElseThrow(() -> new RuntimeException("Receiver not found"));

                String phone = receiver.getPhone(); // debes tener este campo

                String encodedMessage = message.replace(" ", "%20");

                return "https://wa.me/" + phone + "?text=" + encodedMessage;
        }
}
