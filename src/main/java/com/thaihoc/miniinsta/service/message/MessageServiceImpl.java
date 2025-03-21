package com.thaihoc.miniinsta.service.message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.message.MessageRequest;
import com.thaihoc.miniinsta.dto.message.MessageResponse;
import com.thaihoc.miniinsta.dto.profile.ProfileResponse;
import com.thaihoc.miniinsta.exception.MessageNotFoundException;
import com.thaihoc.miniinsta.model.Message;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.repository.MessageRepository;
import com.thaihoc.miniinsta.service.FileService;
import com.thaihoc.miniinsta.service.profile.ProfileService;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private FileService fileService;

    @Override
    @Transactional
    public MessageResponse sendMessage(UserPrincipal userPrincipal, int recipientId, MessageRequest request) {
        Profile sender = profileService.getCurrentUserProfile(userPrincipal);
        Profile recipient = profileService.getProfileById(recipientId);

        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(request.getContent());
        message.setCreatedAt(LocalDateTime.now());
        message.setRead(false);

        if (request.getBase64Image() != null && !request.getBase64Image().isEmpty()) {
            String imageUrl = fileService.uploadImage(request.getBase64Image());
            message.setImageUrl(imageUrl);
        }

        Message savedMessage = messageRepository.save(message);
        return convertToMessageResponse(savedMessage);
    }

    @Override
    public Page<MessageResponse> getConversation(UserPrincipal userPrincipal, int otherProfileId, Pageable pageable) {
        Profile currentProfile = profileService.getCurrentUserProfile(userPrincipal);
        Profile otherProfile = profileService.getProfileById(otherProfileId);

        Page<Message> conversation = messageRepository.findConversation(currentProfile, otherProfile, pageable);

        // Mark messages as read
        List<Integer> unreadMessageIds = conversation.getContent().stream()
                .filter(m -> !m.isRead() && m.getRecipient().getId() == currentProfile.getId())
                .map(Message::getId)
                .collect(Collectors.toList());

        if (!unreadMessageIds.isEmpty()) {
            messageRepository.markAsRead(unreadMessageIds);
        }

        return conversation.map(this::convertToMessageResponse);
    }

    @Override
    public List<MessageResponse> getUnreadMessages(UserPrincipal userPrincipal) {
        Profile profile = profileService.getCurrentUserProfile(userPrincipal);
        List<Message> unreadMessages = messageRepository.findUnreadMessages(profile);

        return unreadMessages.stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markMessagesAsRead(UserPrincipal userPrincipal, List<Integer> messageIds) {
        // No need to get profile as messageRepository.markAsRead handles it
        messageRepository.markAsRead(messageIds);
    }

    @Override
    public long countUnreadMessages(UserPrincipal userPrincipal) {
        Profile profile = profileService.getCurrentUserProfile(userPrincipal);
        return messageRepository.countUnreadMessages(profile);
    }

    @Override
    public Page<ProfileResponse> getRecentConversations(UserPrincipal userPrincipal, Pageable pageable) {
        Profile profile = profileService.getCurrentUserProfile(userPrincipal);
        Page<Profile> conversations = messageRepository.findRecentConversationPartners(profile, pageable);

        return conversations.map(this::convertToProfileResponse);
    }

    @Override
    public Page<MessageResponse> searchMessages(UserPrincipal userPrincipal, int otherProfileId,
            String q, Pageable pageable) {
        Profile currentProfile = profileService.getCurrentUserProfile(userPrincipal);
        Profile otherProfile = profileService.getProfileById(otherProfileId);

        Page<Message> messages = messageRepository.searchMessages(currentProfile, otherProfile, q, pageable);
        return messages.map(this::convertToMessageResponse);
    }

    @Override
    @Transactional
    public void deleteMessage(UserPrincipal userPrincipal, int messageId) {
        Profile profile = profileService.getCurrentUserProfile(userPrincipal);
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + messageId));

        // Only allow sender to delete message
        if (message.getSender().getId() != profile.getId()) {
            throw new RuntimeException("You don't have permission to delete this message");
        }

        messageRepository.delete(message);
    }

    // Helper methods
    private MessageResponse convertToMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .sender(convertToProfileResponse(message.getSender()))
                .recipient(convertToProfileResponse(message.getRecipient()))
                .createdAt(message.getCreatedAt())
                .isRead(message.isRead())
                .imageUrl(message.getImageUrl())
                .build();
    }

    private ProfileResponse convertToProfileResponse(Profile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .username(profile.getUsername())
                .displayName(profile.getDisplayName())
                .bio(profile.getBio())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .isVerified(profile.isVerified())
                .build();
    }
}