package com.thaihoc.miniinsta.service.chat;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.thaihoc.miniinsta.dto.ResultPaginationDTO;
import com.thaihoc.miniinsta.dto.chat.CreateMessageRequest;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Conversation;
import com.thaihoc.miniinsta.model.Message;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.model.enums.MessageType;
import com.thaihoc.miniinsta.repository.MessageRepository;
import com.thaihoc.miniinsta.service.user.ProfileService;
import com.thaihoc.miniinsta.service.FileService;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ConversationService conversationService;
    private final ProfileService profileService;
    private final FileService fileService;

    public MessageServiceImpl(MessageRepository messageRepository, ConversationService conversationService,
            ProfileService profileService, FileService fileService) {
        this.messageRepository = messageRepository;
        this.conversationService = conversationService;
        this.profileService = profileService;
        this.fileService = fileService;
    }

    @Override
    public ResultPaginationDTO getMessagesByConversationId(long conversationId, Pageable pageable) {
        Page<Message> pageMessage = this.messageRepository.findAllByConversationId(conversationId, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageMessage.getTotalPages());
        mt.setTotal(pageMessage.getTotalElements());
        rs.setMeta(mt);
        List<Message> listMessage = pageMessage.getContent();
        rs.setResult(listMessage);
        return rs;
    }

    @Override
    public Message createMessage(long conversationId, CreateMessageRequest request) throws IdInvalidException {
        if (request.getType() == MessageType.IMAGE) {
            String imageUrl = this.fileService.uploadImage(request.getContent());
            request.setContent(imageUrl);
        }
        Conversation conversation = conversationService.getConversationById(conversationId);
        Profile sender = profileService.getProfileById(request.getSenderId());
        Message message = Message.builder()
                .content(request.getContent())
                .conversation(conversation)
                .sender(sender)
                .type(request.getType())
                .build();
        return messageRepository.save(message);
    }

    @Override
    public void deleteMessage(long conversationId, long messageId) throws IdInvalidException {
        Message message = getMessageById(messageId, conversationId);
        messageRepository.delete(message);
    }

    @Override
    public Message getMessageById(long messageId, long conversationId) throws IdInvalidException {
        return messageRepository.findByIdAndConversationId(messageId, conversationId)
                .orElseThrow(() -> new IdInvalidException("Message not found"));
    }

    @Override
    public Message getLastMessageByConversationId(long conversationId) {
        return messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conversationId)
                .orElse(null);
    }
}
