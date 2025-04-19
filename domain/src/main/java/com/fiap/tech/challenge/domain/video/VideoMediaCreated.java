package com.fiap.tech.challenge.domain.video;



import com.fiap.tech.challenge.domain.events.DomainEvent;
import com.fiap.tech.challenge.domain.utils.InstantUtils;

import java.time.Instant;

public record VideoMediaCreated(
        String id,
        String clientId,
        String resourceId,
        String filePath,
        Instant occurredOn
) implements DomainEvent {

    public VideoMediaCreated(final String id, final String clientId, final String resourceId, final String filePath) {
        this(id, clientId, resourceId, filePath, InstantUtils.now());
    }
}
