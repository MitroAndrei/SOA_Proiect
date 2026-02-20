package org.example.faasservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.faasservice.engine.FunctionEngine;
import org.example.faasservice.model.FunctionRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaFunctionTrigger {

    private final FunctionEngine engine;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "faas-triggers", groupId = "faas-service")
    @SuppressWarnings("unchecked")
    public void handleTrigger(String message) {
        try {
            Map<String, Object> trigger = objectMapper.readValue(message, Map.class);

            String functionName = (String) trigger.get("functionName");
            Map<String, Object> payload = (Map<String, Object>) trigger.get("payload");

            log.info("Kafka trigger for function: {}", functionName);

            FunctionRequest request = FunctionRequest.builder()
                    .functionName(functionName)
                    .payload(payload)
                    .build();

            engine.invoke(functionName, request);

        } catch (Exception e) {
            log.error("Failed to process trigger: {}", e.getMessage());
        }
    }
}