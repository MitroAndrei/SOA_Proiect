package org.example.faasservice.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.faasservice.config.FunctionRegistry;
import org.example.faasservice.model.FunctionRequest;
import org.example.faasservice.model.FunctionResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FunctionEngine {

    private final FunctionRegistry registry;

    public FunctionResponse invoke(String functionName, FunctionRequest request) {
        return registry.getFunction(functionName)
                .map(fn -> {
                    long start = System.currentTimeMillis();

                    log.info("Invoking function: {}", functionName);
                    FunctionResponse response = fn.execute(request);

                    long duration = System.currentTimeMillis() - start;
                    response.setExecutionTimeMs(duration);

                    log.info("Function {} completed in {}ms", functionName, duration);
                    return response;
                })
                .orElseGet(() -> {
                    log.warn("Function not found: {}", functionName);
                    return FunctionResponse.builder()
                            .functionName(functionName)
                            .status("ERROR")
                            .result(Map.of("error", "Function not found: " + functionName))
                            .executionTimeMs(0)
                            .executedAt(LocalDateTime.now())
                            .build();
                });
    }
}