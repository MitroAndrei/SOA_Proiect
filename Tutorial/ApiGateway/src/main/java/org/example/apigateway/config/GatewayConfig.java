package org.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Configuration
public class GatewayConfig {
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allowed origins (your frontend URLs)
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",    // Angular dev server
                "http://localhost:3000",    // Shell
                "http://localhost:3001",    // Auth MFE
                "http://localhost:3002",    // Orders MFE
                "http://localhost:3003",    // Dashboard MFE
                "http://localhost"          // Nginx (production)
        ));

        // Allowed HTTP methods
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Allowed headers
        config.setAllowedHeaders(List.of("*"));

        // Allow cookies/auth headers
        config.setAllowCredentials(true);

        // How long the preflight response is cached (1 hour)
        config.setMaxAge(3600L);

        // Expose headers the frontend can read
        config.setExposedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
