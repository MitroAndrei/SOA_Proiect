package org.example.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.model.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final SseService sseService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @KafkaListener(topics = "order-events", groupId = "notification-service")
    public void handleOrderCreated(String message) {

        try {
            OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
            log.info("Received Kafka event - Order: {} for user: {}",
                    event.getOrderId(), event.getUserId());
            // Push to ALL connected devices of this user
            sseService.sendToUser(event.getUserId(), event);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize message: {}", e.getMessage());
        }
    }
}