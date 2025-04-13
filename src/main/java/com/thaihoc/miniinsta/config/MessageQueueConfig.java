package com.thaihoc.miniinsta.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class MessageQueueConfig {
  @Value("${rabbitmq.queue.notification}")
  private String notificationQueue;

  @Value("${rabbitmq.exchange.name}")
  private String notificationExchange;

  public static final String RK_PROFILE_NOTIFICATION = "profile.notification.#";
  public static final String RK_POST_NOTIFICATION = "post.notification.#";
  public static final String RK_COMMENT_NOTIFICATION = "comment.notification.#";

  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    // Define your retry policies and backoff strategies here
    // For example:
    retryTemplate.setRetryPolicy(new SimpleRetryPolicy(2));
    // retryTemplate.setBackOffPolicy(new ExponentialBackOffPolicy());

    return retryTemplate;
  }

  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
      RetryTemplate retryTemplate) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setRetryTemplate(retryTemplate);
    return factory;
  }

  @Bean
  TopicExchange eventExchange() {
    return new TopicExchange(notificationExchange);
  }

  @Bean
  Queue notificationQueue() {
    return new Queue(notificationQueue, true);
  }

  @Bean
  Binding profileNotificationBinding(Queue notificationQueue, TopicExchange eventExchange) {
    return BindingBuilder.bind(notificationQueue).to(eventExchange).with(RK_PROFILE_NOTIFICATION);
  }

  @Bean
  Binding postNotificationBinding(Queue notificationQueue, TopicExchange eventExchange) {
    return BindingBuilder.bind(notificationQueue).to(eventExchange).with(RK_POST_NOTIFICATION);
  }

  @Bean
  Binding commentNotificationBinding(Queue notificationQueue, TopicExchange eventExchange) {
    return BindingBuilder.bind(notificationQueue).to(eventExchange).with(RK_COMMENT_NOTIFICATION);
  }

  // @Bean
  // @Qualifier(PRIVATE_CHAT_QUEUE)
  // Queue privateChatQueue() {
  // return new Queue(PRIVATE_CHAT_QUEUE, false);
  // }

  // @Bean
  // TopicExchange exchange() {
  // return new TopicExchange(CHAT_EXCHANGE);
  // }

  // @Bean
  // Binding privateBinding(@Qualifier(PRIVATE_CHAT_QUEUE) Queue queue,
  // TopicExchange exchange) {
  // return BindingBuilder.bind(queue).to(exchange).with("chat.private.#");
  // }

}
