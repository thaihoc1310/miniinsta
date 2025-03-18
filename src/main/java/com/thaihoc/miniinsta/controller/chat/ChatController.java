package com.thaihoc.miniinsta.controller.chat;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.thaihoc.miniinsta.config.MessageQueueConfig;
import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.dto.message.ChatMessage;
import com.thaihoc.miniinsta.dto.message.SendMessageInput;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ChatController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @MessageMapping("/chat")
    public void sendMessage(@Payload SendMessageInput message, Authentication authentication) {
        UserPrincipal currentUser = (UserPrincipal) authentication.getPrincipal(); // Get username from auth
        log.info("Received message from {}: {}", currentUser.getUsername(), message.getContent());

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(currentUser.getUsername())
                .receiver(message.getReceiver())
                .content(message.getContent())
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();

        // Send to RabbitMQ for handling by the MessageListener
        rabbitTemplate.convertAndSend(MessageQueueConfig.CHAT_EXCHANGE,
                "chat.private." + message.getReceiver(), chatMessage);

        log.info("Message sent to exchange: {}", chatMessage);
    }
}