package com.thaihoc.miniinsta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}
