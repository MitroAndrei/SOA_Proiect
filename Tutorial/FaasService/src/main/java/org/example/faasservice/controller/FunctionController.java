package org.example.faasservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.faasservice.config.FunctionRegistry;
import org.example.faasservice.engine.FunctionEngine;
import org.example.faasservice.model.FunctionRequest;
import org.example.faasservice.model.FunctionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/functions")
@RequiredArgsConstructor
public class FunctionController {

    private final FunctionEngine engine;
    private final FunctionRegistry registry;

    @PostMapping("/{name}")
    public ResponseEntity<FunctionResponse> invoke(
            @PathVariable String name,
            @RequestBody Map<String, Object> payload) {

        FunctionRequest request = FunctionRequest.builder()
                .functionName(name)
                .payload(payload)
                .build();

        FunctionResponse response = engine.invoke(name, request);

        if ("ERROR".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listFunctions() {
        List<String> functionNames = registry.getAllFunctions()
                .keySet().stream().sorted().toList();

        return ResponseEntity.ok(Map.of(
                "availableFunctions", functionNames,
                "count", functionNames.size()
        ));
    }
}