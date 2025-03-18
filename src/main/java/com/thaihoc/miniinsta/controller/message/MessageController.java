package com.thaihoc.miniinsta.controller.message;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send/{recipientId}")
    public ResponseEntity<MessageResponse> sendMessage(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int recipientId, @Valid @RequestBody MessageRequest request) {
        MessageResponse response = messageService.sendMessage(userPrincipal, recipientId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversation/{otherProfileId}")
    public ResponseEntity<Page<MessageResponse>> getConversation(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int otherProfileId, @PageableDefault(size = 20) Pageable pageable) {
        Page<MessageResponse> conversation = messageService.getConversation(userPrincipal, otherProfileId, pageable);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<MessageResponse>> getUnreadMessages(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<MessageResponse> unreadMessages = messageService.getUnreadMessages(userPrincipal);
        return ResponseEntity.ok(unreadMessages);
    }

    @PostMapping("/mark-read")
    public ResponseEntity<Void> markMessagesAsRead(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody List<Integer> messageIds) {
        messageService.markMessagesAsRead(userPrincipal, messageIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnreadMessages(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        long count = messageService.countUnreadMessages(userPrincipal);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/recent-conversations")
    public ResponseEntity<Page<ProfileResponse>> getRecentConversations(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PageableDefault(size = 20) Pageable pageable) {
        Page<ProfileResponse> conversations = messageService.getRecentConversations(userPrincipal, pageable);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/search/{otherProfileId}")
    public ResponseEntity<Page<MessageResponse>> searchMessages(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int otherProfileId, @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<MessageResponse> messages = messageService.searchMessages(userPrincipal, otherProfileId, searchTerm,
                pageable);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int messageId) {
        messageService.deleteMessage(userPrincipal, messageId);
        return ResponseEntity.ok().build();
    }
}
