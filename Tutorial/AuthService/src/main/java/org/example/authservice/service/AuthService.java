package org.example.authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.authservice.model.dto.AuthRequest;
import org.example.authservice.model.dto.AuthResponse;
import org.example.authservice.model.dto.RegisterRequest;
import org.springframework.stereotype.Service;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    boolean validateToken(String token);
    String extractUsername(String token);
}
