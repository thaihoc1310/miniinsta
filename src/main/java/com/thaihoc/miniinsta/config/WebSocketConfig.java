package com.thaihoc.miniinsta.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	// @Value("${spring.rabbitmq.stomp.relay.host}")
	private String relayHost = "localhost";

	// @Value("${spring.rabbitmq.stomp.relay.port}")
	private int relayPort = 61613;

	// @Value("${spring.rabbitmq.stomp.relay.login}")
	private String relayLogin = "guest";

	// @Value("${spring.rabbitmq.stomp.relay.passcode}")
	private String relayPasscode = "guest";

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// config.enableSimpleBroker("/topic");
		config.enableStompBrokerRelay("/topic", "/queue")
				.setRelayHost(relayHost)
				.setRelayPort(relayPort)
				.setClientLogin(relayLogin)
				.setClientPasscode(relayPasscode)
				.setUserDestinationBroadcast("/topic/unresolved-user")
				.setUserRegistryBroadcast("/topic/registry-broadcast");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws");
	}

}
