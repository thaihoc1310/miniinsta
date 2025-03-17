package com.thaihoc.miniinsta.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.Notification;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.enums.NotificationType;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Page<Notification> findByRecipientOrderByCreatedAtDesc(Profile recipient, Pageable pageable);

    List<Notification> findTop10ByRecipientAndIsReadFalseOrderByCreatedAtDesc(Profile recipient);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.id = :profileId AND n.isRead = false")
    long countUnreadNotifications(@Param("profileId") Integer profileId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id IN :ids")
    void markAsRead(@Param("ids") List<Integer> notificationIds);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipient.id = :profileId")
    void markAllAsRead(@Param("profileId") Integer profileId);

    Page<Notification> findByRecipientAndType(Profile recipient, NotificationType type, Pageable pageable);
}
