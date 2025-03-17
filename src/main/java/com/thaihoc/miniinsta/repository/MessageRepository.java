package com.thaihoc.miniinsta.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.Message;
import com.thaihoc.miniinsta.model.Profile;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender = :profile1 AND m.recipient = :profile2) OR " +
            "(m.sender = :profile2 AND m.recipient = :profile1) " +
            "ORDER BY m.createdAt DESC")
    Page<Message> findConversation(
            @Param("profile1") Profile profile1,
            @Param("profile2") Profile profile2,
            Pageable pageable);

    @Query("SELECT m FROM Message m WHERE " +
            "m.recipient = :profile AND m.isRead = false " +
            "ORDER BY m.createdAt DESC")
    List<Message> findUnreadMessages(@Param("profile") Profile profile);

    @Query("SELECT COUNT(m) FROM Message m WHERE " +
            "m.recipient = :profile AND m.isRead = false")
    long countUnreadMessages(@Param("profile") Profile profile);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE " +
            "m.id IN :messageIds")
    void markAsRead(@Param("messageIds") List<Integer> messageIds);

    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.recipient = :profile " +
            "ORDER BY MAX(m.createdAt) DESC")
    Page<Profile> findRecentConversationPartners(@Param("profile") Profile profile, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE " +
            "((m.sender = :profile1 AND m.recipient = :profile2) OR " +
            "(m.sender = :profile2 AND m.recipient = :profile1)) AND " +
            "m.content LIKE %:searchTerm%")
    Page<Message> searchMessages(
            @Param("profile1") Profile profile1,
            @Param("profile2") Profile profile2,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);
}