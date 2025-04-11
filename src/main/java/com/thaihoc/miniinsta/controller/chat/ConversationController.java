package com.thaihoc.miniinsta.controller.chat;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.chat.CreateConversationRequest;
import com.thaihoc.miniinsta.dto.chat.UpdateConversationRequest;
import com.thaihoc.miniinsta.exception.AlreadyExistsException;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Conversation;
import com.thaihoc.miniinsta.service.chat.ConversationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping("/conversations")
    public ResponseEntity<Conversation> createConversation(@Valid @RequestBody CreateConversationRequest request)
            throws IdInvalidException, AlreadyExistsException {
        return ResponseEntity.ok(conversationService.createConversation(request));
    }

    @GetMapping("/profiles/{profileId}/conversations")
    public ResponseEntity<ResultPaginationDTO> getAllConversationsByProfileId(@PathVariable long profileId,
            @RequestParam(required = false) String q, Pageable pageable) throws IdInvalidException {
        return ResponseEntity.ok(conversationService.getAllConversationsByProfileId(profileId, q, pageable));
    }

    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<Conversation> getConversationById(@PathVariable long conversationId)
            throws IdInvalidException {
        return ResponseEntity.ok(conversationService.getConversationById(conversationId));
    }

    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<Void> deleteConversationById(@PathVariable long conversationId)
            throws IdInvalidException {
        conversationService.deleteConversationById(conversationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/conversations/{conversationId}")
    public ResponseEntity<Conversation> updateConversation(@PathVariable long conversationId,
            @RequestBody UpdateConversationRequest request) throws IdInvalidException {
        return ResponseEntity.ok(conversationService.updateConversation(conversationId, request));
    }

}
