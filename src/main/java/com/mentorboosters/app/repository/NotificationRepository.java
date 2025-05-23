package com.mentorboosters.app.repository;

import com.mentorboosters.app.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMentorIdAndIsRead(Long mentorId, Boolean isRead);

    List<Notification> findByRecipientIdAndIsRead(Long userId, boolean isRead);

    List<Notification> findByIsRead(Boolean isRead);
}
