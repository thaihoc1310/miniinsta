package com.thaihoc.miniinsta.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.Notification;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.enums.NotificationType;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    Optional<Notification> findById(int id);

    Page<Notification> findByRecipientOrderByCreatedAtDesc(Profile recipient, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.id = :profileId AND n.isRead = false")
    int countUnreadNotifications(@Param("profileId") long profileId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipient.id = :profileId")
    void markAllAsRead(@Param("profileId") long profileId);

    Page<Notification> findByRecipientAndType(Profile recipient, NotificationType type, Pageable pageable);
}
