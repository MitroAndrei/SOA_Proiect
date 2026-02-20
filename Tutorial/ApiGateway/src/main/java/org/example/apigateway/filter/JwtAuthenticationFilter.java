package org.example.apigateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final WebClient webClient;

    private final List<String> openPaths = List.of(
            "/api/auth/login",
            "/api/auth/register"
    );

    // Constructor injection using the bean we defined
    public JwtAuthenticationFilter(Builder webClientBuilder, @Value("${auth.service.url}") String authServiceUrl) {
        this.webClient = webClientBuilder
                .baseUrl(authServiceUrl)
                .build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Skip auth for open endpoints
        if (isOpenPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/auth/validate")
                        .queryParam("token", token)
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .defaultIfEmpty(false)
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        // Optionally forward user info to downstream services
                        return chain.filter(exchange);
                    }
                    return unauthorized(exchange);
                })
                .onErrorResume(e -> {
                    System.err.println("Auth validation error: " + e.getMessage());
                    return unauthorized(exchange);
                });
    }

    private boolean isOpenPath(String path) {
        return openPaths.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}