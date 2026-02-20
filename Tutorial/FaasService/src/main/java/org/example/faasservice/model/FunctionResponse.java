package org.example.faasservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionResponse {
    private String functionName;
    private String status;
    private Map<String, Object> result;
    private long executionTimeMs;
    private LocalDateTime executedAt;
}