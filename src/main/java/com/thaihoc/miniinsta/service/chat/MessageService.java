package com.thaihoc.miniinsta.service.chat;

import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.chat.CreateMessageRequest;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Message;

public interface MessageService {

    ResultPaginationDTO getMessagesByConversationId(long conversationId, Pageable pageable);

    Message createMessage(long conversationId, CreateMessageRequest request) throws IdInvalidException;

    void deleteMessage(long conversationId, long messageId) throws IdInvalidException;

    Message getMessageById(long messageId, long conversationId) throws IdInvalidException;

    Message getLastMessageByConversationId(long conversationId);
}
