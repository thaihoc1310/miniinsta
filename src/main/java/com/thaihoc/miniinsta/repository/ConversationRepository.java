package com.thaihoc.miniinsta.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.Conversation;

@Repository
public interface ConversationRepository
                extends JpaRepository<Conversation, Long>, JpaSpecificationExecutor<Conversation> {

        @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.profile.id = :profileId AND LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))")
        Page<Conversation> getAllByParticipantsByProfileIdAndNameContaining(long profileId, String q,
                        Pageable pageable);

}
