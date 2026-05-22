package com.jobcol.backend.NotificationService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jobcol.backend.NotificationService.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdAndReadFalse(Long userId);

    Long countByUserIdAndReadFalse(Long userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

}
