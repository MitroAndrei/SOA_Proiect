package org.example.faasservice.config;

import lombok.extern.slf4j.Slf4j;
import org.example.faasservice.functions.FaasFunction;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class FunctionRegistry {

    private final Map<String, FaasFunction> functions = new HashMap<>();

    // Spring injects ALL beans that implement FaasFunction
    public FunctionRegistry(List<FaasFunction> functionList) {
        functionList.forEach(fn -> {
            functions.put(fn.getName(), fn);
            log.info("Registered function: {}", fn.getName());
        });
        log.info("Total functions registered: {}", functions.size());
    }

    public Optional<FaasFunction> getFunction(String name) {
        return Optional.ofNullable(functions.get(name));
    }

    public Map<String, FaasFunction> getAllFunctions() {
        return functions;
    }
}