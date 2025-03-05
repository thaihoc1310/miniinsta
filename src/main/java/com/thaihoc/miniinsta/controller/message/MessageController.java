package com.thaihoc.miniinsta.controller.message;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thaihoc.miniinsta.config.MessageQueueConfig;
import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.message.SendMessageInput;
import com.thaihoc.miniinsta.exception.UserNotFoundException;
import com.thaihoc.miniinsta.model.ChatMessage;
import com.thaihoc.miniinsta.model.User;
import com.thaihoc.miniinsta.repository.ChatMessageRepository;
import com.thaihoc.miniinsta.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {
  private final UserRepository userRepository;

  private final ChatMessageRepository chatMessageRepository;

  private final RabbitTemplate rabbitTemplate;

  private final ObjectMapper objectMapper;

  private final SimpMessagingTemplate messagingTemplate;

  private final SimpUserRegistry simpUserRegistry;

  @MessageMapping("/chat")
  @SendToUser("/queue/messages")
  public ChatMessage sendMessage(SendMessageInput input, Authentication authentication) throws JsonProcessingException {
    log.info("got input {}", input);
    // find the receiver user
    Optional<User> receiver = userRepository.findByUsername(input.getReceiver());
    if (!receiver.isPresent()) {
      throw new UserNotFoundException();
    }
    // create a chat message record
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    ChatMessage chatMessage = chatMessageRepository
        .save(ChatMessage.builder().content(input.getContent()).receiver(input.getReceiver())
            .sender(userPrincipal.getId().toString()).timestamp(LocalDateTime.now()).build());
    // send chat message to topic exchange
    String routingKey = "chat.private." + input.getReceiver();

    boolean isReceiverOnline = simpUserRegistry.getUser(input.getReceiver()) != null;
    if (isReceiverOnline) {
      messagingTemplate.convertAndSendToUser(
          chatMessage.getReceiver(), "/queue/messages", chatMessage);
      log.info("send message to receiver = {}, message = {}", input.getReceiver(), chatMessage);
    } else {
      rabbitTemplate.convertAndSend(MessageQueueConfig.CHAT_EXCHANGE, routingKey,
          objectMapper.writeValueAsString(chatMessage));
      log.info("sent message to chat exchange = {}, routing Key = {}, message = {}",
          MessageQueueConfig.CHAT_EXCHANGE,
          routingKey, chatMessage);
    }
    return chatMessage;
  }
}
