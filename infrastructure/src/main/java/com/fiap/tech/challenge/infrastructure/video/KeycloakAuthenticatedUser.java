package com.fiap.tech.challenge.infrastructure.video;

import com.fiap.tech.challenge.domain.video.AuthenticatedUser;
import com.fiap.tech.challenge.domain.video.ClientID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class KeycloakAuthenticatedUser implements AuthenticatedUser {

    @Override
    public ClientID getClientId() {
        var authentication = SecurityContextHolder
                .getContext().getAuthentication() != null ? SecurityContextHolder
                .getContext().getAuthentication().getName() : null;


        return authentication != null ? ClientID.from(authentication.replace("-", "")) : null;
    }
}
