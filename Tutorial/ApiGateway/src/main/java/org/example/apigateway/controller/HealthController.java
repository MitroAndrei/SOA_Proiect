package org.example.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/gateway/health")
    public Mono<Map<String, String>> health() {
        String hostname;
        String ip;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            hostname = "unknown";
            ip = "unknown";
        }

        return Mono.just(Map.of(
                "status", "UP",
                "hostname", hostname,
                "ip", ip
        ));
    }
}