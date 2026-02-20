package org.example.faasservice.functions;

import lombok.extern.slf4j.Slf4j;
import org.example.faasservice.model.FunctionRequest;
import org.example.faasservice.model.FunctionResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@Slf4j
public class EmailFunction implements FaasFunction {

    @Override
    public String getName() {
        return "email";
    }

    @Override
    public FunctionResponse execute(FunctionRequest request) {
        Map<String, Object> payload = request.getPayload();

        String to = (String) payload.getOrDefault("to", "unknown");
        String subject = (String) payload.getOrDefault("subject", "No Subject");
        String body = (String) payload.getOrDefault("body", "");

        log.info("Sending email to: {} | Subject: {}", to, subject);

        // TODO: Integrate with real email service (SendGrid, SES, etc.)
        // For now, simulate sending
        boolean sent = true;

        return FunctionResponse.builder()
                .functionName(getName())
                .status(sent ? "SUCCESS" : "ERROR")
                .result(Map.of(
                        "to", to,
                        "subject", subject,
                        "sent", sent,
                        "message", sent ? "Email sent successfully" : "Failed to send email"
                ))
                .executedAt(LocalDateTime.now())
                .build();
    }
}