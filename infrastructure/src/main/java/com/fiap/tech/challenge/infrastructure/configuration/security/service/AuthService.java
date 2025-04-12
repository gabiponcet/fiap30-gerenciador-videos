package com.fiap.tech.challenge.infrastructure.configuration.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuthService {

    public String getCurrentUserId(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getSubject(); // O 'sub' no JWT é o ID do usuário
    }
}
