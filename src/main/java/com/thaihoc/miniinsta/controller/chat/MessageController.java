package com.thaihoc.miniinsta.controller.chat;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.message.MessageRequest;
import com.thaihoc.miniinsta.dto.message.MessageResponse;
import com.thaihoc.miniinsta.dto.profile.ProfileResponse;
import com.thaihoc.miniinsta.service.message.MessageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Send message to a user
     */
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            Authentication authentication,
            @Valid @RequestBody MessageRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        MessageResponse response = messageService.sendMessage(userPrincipal, request.getRecipientId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get conversation with a user
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<Page<MessageResponse>> getConversation(
            Authentication authentication,
            @PathVariable int id,
            @PageableDefault(size = 20) Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Page<MessageResponse> conversation = messageService.getConversation(userPrincipal, id, pageable);
        return ResponseEntity.ok(conversation);
    }

    /**
     * Get unread messages
     */
    @GetMapping("/unread")
    public ResponseEntity<List<MessageResponse>> getUnreadMessages(
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<MessageResponse> unreadMessages = messageService.getUnreadMessages(userPrincipal);
        return ResponseEntity.ok(unreadMessages);
    }

    /**
     * Mark messages as read
     */
    @PatchMapping("/read")
    public ResponseEntity<Void> markMessagesAsRead(
            Authentication authentication,
            @RequestBody List<Integer> messageIds) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        messageService.markMessagesAsRead(userPrincipal, messageIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * Count unread messages
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnreadMessages(
            Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        long count = messageService.countUnreadMessages(userPrincipal);
        return ResponseEntity.ok(count);
    }

    /**
     * Get list of recent conversations
     */
    @GetMapping("/conversations")
    public ResponseEntity<Page<ProfileResponse>> getRecentConversations(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Page<ProfileResponse> conversations = messageService.getRecentConversations(userPrincipal, pageable);
        return ResponseEntity.ok(conversations);
    }

    /**
     * Search messages in a conversation
     */
    @GetMapping("/users/{id}/messages")
    public ResponseEntity<Page<MessageResponse>> searchMessages(
            Authentication authentication,
            @PathVariable int id,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Page<MessageResponse> messages = messageService.searchMessages(userPrincipal, id, q, pageable);
        return ResponseEntity.ok(messages);
    }

    /**
     * Delete a message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(
            Authentication authentication,
            @PathVariable int id) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        messageService.deleteMessage(userPrincipal, id);
        return ResponseEntity.noContent().build();
    }
}
