package com.thaihoc.miniinsta.event;

import java.time.LocalDateTime;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thaihoc.miniinsta.config.MessageQueueConfig;
import com.thaihoc.miniinsta.dto.message.ChatMessage;
import com.thaihoc.miniinsta.model.Message;
import com.thaihoc.miniinsta.model.Profile;
import com.thaihoc.miniinsta.repository.MessageRepository;
import com.thaihoc.miniinsta.repository.ProfileRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageListener {
  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private MessageRepository messageRepository;

  @RabbitListener(queues = MessageQueueConfig.PRIVATE_CHAT_QUEUE)
  @Transactional
  public void handlePrivateMessage(String message) throws JsonMappingException, JsonProcessingException {
    log.info("Listener got message {}", message);
    ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);

    // Save the message to the database first
    try {
      Profile sender = profileRepository.findByUsername(chatMessage.getSender())
          .orElseThrow(() -> new RuntimeException("Sender profile not found"));

      Profile recipient = profileRepository.findByUsername(chatMessage.getReceiver())
          .orElseThrow(() -> new RuntimeException("Recipient profile not found"));

      Message dbMessage = new Message();
      dbMessage.setSender(sender);
      dbMessage.setRecipient(recipient);
      dbMessage.setContent(chatMessage.getContent());
      dbMessage.setImageUrl(chatMessage.getImageUrl());
      dbMessage.setCreatedAt(chatMessage.getTimestamp() != null ? chatMessage.getTimestamp() : LocalDateTime.now());
      dbMessage.setRead(false);

      Message savedMessage = messageRepository.save(dbMessage);
      log.info("Message persisted to database with ID: {}", savedMessage.getId());

      // After successful database save, send WebSocket message
      messagingTemplate.convertAndSendToUser(
          chatMessage.getReceiver(), "/queue/messages", chatMessage);
      log.info("Listener sent message to Receiver = {}", chatMessage.getReceiver());

    } catch (Exception e) {
      log.error("Error processing message: {}", e.getMessage(), e);

      throw new AmqpRejectAndDontRequeueException("Error saving message to database", e);
    }
  }
}
