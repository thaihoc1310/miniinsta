package com.thaihoc.miniinsta.service.message;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.message.MessageRequest;
import com.thaihoc.miniinsta.dto.message.MessageResponse;
import com.thaihoc.miniinsta.dto.profile.ProfileResponse;

public interface MessageService {

        // Gửi tin nhắn
        MessageResponse sendMessage(UserPrincipal userPrincipal, int recipientId, MessageRequest request);

        // Lấy cuộc hội thoại với một người dùng
        Page<MessageResponse> getConversation(UserPrincipal userPrincipal, int otherProfileId, Pageable pageable);

        // Lấy tất cả tin nhắn chưa đọc
        List<MessageResponse> getUnreadMessages(UserPrincipal userPrincipal);

        // Đánh dấu tin nhắn đã đọc
        void markMessagesAsRead(UserPrincipal userPrincipal, List<Integer> messageIds);

        // Đếm số tin nhắn chưa đọc
        long countUnreadMessages(UserPrincipal userPrincipal);

        // Lấy danh sách người dùng đã nhắn tin gần đây
        Page<ProfileResponse> getRecentConversations(UserPrincipal userPrincipal, Pageable pageable);

        // Tìm kiếm trong cuộc hội thoại
        Page<MessageResponse> searchMessages(UserPrincipal userPrincipal, int otherProfileId,
                        String q, Pageable pageable);

        // Xóa tin nhắn
        void deleteMessage(UserPrincipal userPrincipal, int messageId);
}