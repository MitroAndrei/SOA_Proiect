package org.example.processingservice.service.kafka;

import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FaasTriggerPublisher {

    private static final String TOPIC = "faas-triggers";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void triggerFunction(String functionName, Map<String, Object> payload) {
        try {
            Map<String, Object> message = Map.of(
                    "functionName", functionName,
                    "payload", payload
            );
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(TOPIC, functionName, json);
            log.info("Published FaaS trigger for function: {}", functionName);
        } catch (Exception e) {
            log.error("Failed to publish FaaS trigger: {}", e.getMessage());
        }
    }

    public void triggerEmail(String to, String subject, String body) {
        triggerFunction("email", Map.of(
                "to", to,
                "subject", subject,
                "body", body
        ));
    }
}