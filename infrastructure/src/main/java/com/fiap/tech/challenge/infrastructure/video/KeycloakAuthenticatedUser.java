package com.fiap.tech.challenge.infrastructure.video;

import com.fiap.tech.challenge.domain.video.AuthenticatedUser;
import com.fiap.tech.challenge.domain.video.ClientID;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class KeycloakAuthenticatedUser implements AuthenticatedUser {

    @Override
    public ClientID getClientId() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken token)) {
            throw new IllegalStateException("Authentication must be of type JwtAuthenticationToken");
        }

        // Use o claim correto do token para representar o clientId — aqui usamos o "sub"
        return ClientID.from(token.getToken().getSubject());  // ou token.getToken().getClaim("preferred_username")
    }
}
