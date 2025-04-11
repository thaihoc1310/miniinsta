package com.thaihoc.miniinsta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.thaihoc.miniinsta.model.Participant;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long>, JpaSpecificationExecutor<Participant> {

    boolean existsByProfileIdAndConversationId(long profileId, long conversationId);
}
