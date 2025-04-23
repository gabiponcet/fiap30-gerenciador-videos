package com.fiap.tech.challenge.infrastructure.video;

import com.fiap.tech.challenge.domain.video.ClientID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakAuthenticatedUserTest {
    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getClientIdShouldReturnClientIdWithoutHyphens() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("1234-5678-9012");

        KeycloakAuthenticatedUser keycloakAuthenticatedUser = new KeycloakAuthenticatedUser();
        ClientID clientId = keycloakAuthenticatedUser.getClientId();

        assertEquals("123456789012", clientId.getValue());
    }


}