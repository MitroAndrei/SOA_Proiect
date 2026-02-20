package org.example.processingservice.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.processingservice.model.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private static final String TOPIC = "order-events";
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public void publish(OrderCreatedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(TOPIC, event.getUserId(), json)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Published order event: {}", event.getOrderId());
                        } else {
                            log.error("Failed to publish: {}", ex.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to serialize event: {}", e.getMessage());
        }
    }
}