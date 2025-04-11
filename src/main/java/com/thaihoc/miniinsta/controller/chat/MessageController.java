package com.thaihoc.miniinsta.controller.chat;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.chat.CreateMessageRequest;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Message;
import com.thaihoc.miniinsta.service.chat.MessageService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/conversations")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<ResultPaginationDTO> getMessagesByConversationId(@PathVariable long conversationId,
            Pageable pageable) {
        return ResponseEntity.ok(messageService.getMessagesByConversationId(conversationId, pageable));
    }

    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<Message> createMessage(@PathVariable long conversationId,
            @Valid @RequestBody CreateMessageRequest request) throws IdInvalidException {
        return ResponseEntity.ok(messageService.createMessage(conversationId, request));
    }

    @DeleteMapping("/{conversationId}/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable long conversationId, @PathVariable long messageId)
            throws IdInvalidException {
        messageService.deleteMessage(conversationId, messageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{conversationId}/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable long conversationId, @PathVariable long messageId)
            throws IdInvalidException {
        return ResponseEntity.ok(messageService.getMessageById(messageId, conversationId));
    }

}
