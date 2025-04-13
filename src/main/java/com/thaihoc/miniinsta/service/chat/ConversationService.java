package com.thaihoc.miniinsta.service.chat;

import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.chat.CreateConversationRequest;
import com.thaihoc.miniinsta.dto.chat.UpdateConversationRequest;
import com.thaihoc.miniinsta.exception.AlreadyExistsException;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Conversation;

public interface ConversationService {

    Conversation getConversationById(long conversationId) throws IdInvalidException;

    Conversation createConversation(CreateConversationRequest request)
            throws IdInvalidException, AlreadyExistsException;

    ResultPaginationDTO getAllConversationsByProfileId(long profileId, String q, Pageable pageable);

    void deleteConversationById(long conversationId) throws IdInvalidException;

    Conversation updateConversation(long conversationId, UpdateConversationRequest request) throws IdInvalidException;

}
