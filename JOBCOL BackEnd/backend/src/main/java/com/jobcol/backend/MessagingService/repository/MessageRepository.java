package com.jobcol.backend.MessagingService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobcol.backend.MessagingService.model.Message;
import com.jobcol.backend.UserService.model.User;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderIdOrderBySentDateDesc(Long senderId);

    List<Message> findByReceiverIdOrderBySentDateDesc(Long receiverId);

    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderBySentDateAsc(
            Long sender1, Long receiver1,
            Long sender2, Long receiver2
    );

    Long countByReceiverIdAndReadFalse(Long receiverId);
    
}
