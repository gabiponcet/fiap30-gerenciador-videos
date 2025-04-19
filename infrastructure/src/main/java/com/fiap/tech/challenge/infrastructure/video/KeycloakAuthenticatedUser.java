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
                .getContext().getAuthentication().getName();


        return ClientID.from(authentication.replace("-", ""));
    }
}
