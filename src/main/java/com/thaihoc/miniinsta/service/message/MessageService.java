package com.thaihoc.miniinsta.service.message;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.message.MessageRequest;
import com.thaihoc.miniinsta.dto.message.MessageResponse;
import com.thaihoc.miniinsta.dto.profile.ProfileResponse;

public interface MessageService {

        // Send message
        MessageResponse sendMessage(UserPrincipal userPrincipal, int recipientId, MessageRequest request);

        // Get conversation with a user
        Page<MessageResponse> getConversation(UserPrincipal userPrincipal, int otherProfileId, Pageable pageable);

        // Get all unread messages
        List<MessageResponse> getUnreadMessages(UserPrincipal userPrincipal);

        // Mark messages as read
        void markMessagesAsRead(UserPrincipal userPrincipal, List<Integer> messageIds);

        // Count unread messages
        long countUnreadMessages(UserPrincipal userPrincipal);

        // Get list of users with recent conversations
        Page<ProfileResponse> getRecentConversations(UserPrincipal userPrincipal, Pageable pageable);

        // Search in conversation
        Page<MessageResponse> searchMessages(UserPrincipal userPrincipal, int otherProfileId,
                        String q, Pageable pageable);

        // Delete message
        void deleteMessage(UserPrincipal userPrincipal, int messageId);
}