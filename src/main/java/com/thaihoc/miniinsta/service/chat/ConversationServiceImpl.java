package com.thaihoc.miniinsta.service.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.chat.ConversationSummaryResponse;
import com.thaihoc.miniinsta.dto.chat.CreateConversationRequest;
import com.thaihoc.miniinsta.dto.chat.CreateParticipantRequest;
import com.thaihoc.miniinsta.dto.chat.UpdateConversationRequest;
import com.thaihoc.miniinsta.exception.AlreadyExistsException;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Conversation;
import com.thaihoc.miniinsta.model.Message;
import com.thaihoc.miniinsta.model.Participant;
import com.thaihoc.miniinsta.model.enums.ConversationType;
import com.thaihoc.miniinsta.model.enums.MessageType;
import com.thaihoc.miniinsta.repository.ConversationRepository;

@Service
public class ConversationServiceImpl implements ConversationService {
    private final ConversationRepository conversationRepository;
    private final ParticipantService participantService;
    private final MessageService messageService;

    public ConversationServiceImpl(ConversationRepository conversationRepository,
            ParticipantService participantService, MessageService messageService) {
        this.conversationRepository = conversationRepository;
        this.participantService = participantService;
        this.messageService = messageService;
    }

    @Override
    public Conversation getConversationById(long conversationId) throws IdInvalidException {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IdInvalidException("Conversation not found"));
    }

    @Override
    @Transactional
    public Conversation createConversation(CreateConversationRequest request)
            throws IdInvalidException, AlreadyExistsException {
        Conversation conversation = new Conversation();
        if (request.getType() == ConversationType.PRIVATE && request.getProfileIds().size() != 2) {
            throw new IdInvalidException("Private conversation must have exactly 2 participants");
        }
        conversation.setType(request.getType());
        conversation.setMessages(new ArrayList<>());
        conversation.setParticipants(new ArrayList<>());
        conversation = this.conversationRepository.save(conversation);
        for (Long profileId : request.getProfileIds()) {
            Participant participant = this.participantService.createParticipant(new CreateParticipantRequest(
                    profileId,
                    conversation.getId()));
            conversation.getParticipants().add(participant);
        }
        return this.conversationRepository.save(conversation);
    }

    @Override
    public ResultPaginationDTO getAllConversationsByProfileId(long profileId, String q, Pageable pageable) {
        Page<Conversation> conversations = this.conversationRepository.getAllByParticipantsByProfileIdAndNameContaining(
                profileId, q, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(conversations.getTotalPages());
        mt.setTotal(conversations.getTotalElements());
        rs.setMeta(mt);
        List<ConversationSummaryResponse> listConversation = conversations.getContent().stream()
                .map(conversation -> convertToConversationSummaryResponse(conversation, profileId))
                .toList();
        rs.setResult(listConversation);
        return rs;
    }

    @Override
    public void deleteConversationById(long conversationId) throws IdInvalidException {
        Conversation conversation = this.getConversationById(conversationId);
        this.conversationRepository.delete(conversation);
    }

    @Override
    public Conversation updateConversation(long conversationId, UpdateConversationRequest request)
            throws IdInvalidException {
        Conversation conversation = this.getConversationById(conversationId);
        if (request.getName() != null && !request.getName().equals(conversation.getName())
                && request.getName().length() > 2) {
            conversation.setName(request.getName());
        }
        return this.conversationRepository.save(conversation);
    }

    private ConversationSummaryResponse convertToConversationSummaryResponse(Conversation conversation,
            long profileId) {
        ConversationSummaryResponse response = new ConversationSummaryResponse();
        response.setConversationId(conversation.getId());
        if (conversation.getType() == ConversationType.PRIVATE) {
            Optional<Participant> participant = conversation.getParticipants().stream()
                    .filter(p -> p.getProfile().getId() != profileId)
                    .findFirst();
            response.setDisplayName(participant.get().getProfile().getDisplayName());
            response.setProfileImageUrl(participant.get().getProfile().getProfilePictureUrl());
        } else {
            response.setDisplayName(conversation.getName());
            response.setProfileImageUrl(null);
        }
        response.setType(conversation.getType());

        Message lastMessage = this.messageService.getLastMessageByConversationId(conversation.getId());
        if (lastMessage != null) {
            if (lastMessage.getType() == MessageType.TEXT) {
                response.setLastMessageContent(lastMessage.getContent());
            } else {
                response.setLastMessageContent("sent an image");
            }
            response.setLastMessageTimestamp(lastMessage.getCreatedAt());
            String lastSenderDisplayName = lastMessage.getSender().getDisplayName();
            if (lastMessage.getSender().getId() == profileId) {
                lastSenderDisplayName = "You";
            }
            response.setLastSenderDisplayName(lastSenderDisplayName);
        }
        return response;
    }

}
