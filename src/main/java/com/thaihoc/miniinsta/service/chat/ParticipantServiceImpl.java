package com.thaihoc.miniinsta.service.chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Lazy;

import com.thaihoc.miniinsta.dto.chat.CreateParticipantRequest;
import com.thaihoc.miniinsta.dto.chat.UpdateParticipantRequest;
import com.thaihoc.miniinsta.exception.AlreadyExistsException;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Conversation;
import com.thaihoc.miniinsta.model.Participant;
import com.thaihoc.miniinsta.model.enums.ConversationType;
import com.thaihoc.miniinsta.repository.ParticipantRepository;
import com.thaihoc.miniinsta.service.user.ProfileService;

@Service
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ProfileService profileService;
    private final ConversationService conversationService;

    public ParticipantServiceImpl(ParticipantRepository participantRepository, ProfileService profileService,
            @Lazy ConversationService conversationService) {
        this.participantRepository = participantRepository;
        this.profileService = profileService;
        this.conversationService = conversationService;
    }

    @Override
    public Participant getParticipantById(long participantId) throws IdInvalidException {
        return participantRepository.findById(participantId)
                .orElseThrow(() -> new IdInvalidException("Participant not found"));
    }

    @Override
    @Transactional
    public Participant createParticipant(CreateParticipantRequest request)
            throws IdInvalidException, AlreadyExistsException {
        if (isParticipantExists(request.getProfileId(), request.getConversationId())) {
            throw new AlreadyExistsException("Participant already exists");
        }
        Conversation conversation = conversationService.getConversationById(request.getConversationId());
        if (conversation.getType() == ConversationType.PRIVATE)
            throw new AlreadyExistsException("Private conversation cannot have more than 2 participants");
        Participant participant = Participant.builder()
                .profile(profileService.getProfileById(request.getProfileId()))
                .conversation(conversation)
                .build();

        return participantRepository.save(participant);
    }

    @Override
    public Participant updateParticipant(UpdateParticipantRequest request) throws IdInvalidException {
        Participant participant = getParticipantById(request.getParticipantId());
        if (request.getNickname() != null && !request.getNickname().equals(participant.getNickname())) {
            participant.setNickname(request.getNickname());
        }

        if (request.getLastReadTimestamp() != null) {
            participant.setLastReadTimestamp(request.getLastReadTimestamp());
        }
        return participantRepository.save(participant);
    }

    @Override
    public void deleteParticipant(long participantId) throws IdInvalidException {
        Participant participant = getParticipantById(participantId);
        participantRepository.delete(participant);
    }

    private boolean isParticipantExists(long profileId, long conversationId) {
        return participantRepository.existsByProfileIdAndConversationId(profileId, conversationId);
    }

}
