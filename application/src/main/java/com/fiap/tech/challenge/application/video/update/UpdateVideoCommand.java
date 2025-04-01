package com.fiap.tech.challenge.application.video.update;


import com.fiap.tech.challenge.domain.resource.Resource;

import java.util.Optional;
import java.util.Set;

public record UpdateVideoCommand(
        String id,
        String title,
        String description,
        Double duration,
        Resource video
) {

    public static UpdateVideoCommand with(
            final String id,
            final String title,
            final String description,
            final Double duration,

            final Resource video
    ) {
        return new UpdateVideoCommand(
                id,
                title,
                description,
                duration,
                video
        );
    }

    public static UpdateVideoCommand with(
            final String id,
            final String title,
            final String description,
            final Double duration) {
        return with(
                id,
                title,
                description,
                duration,
                null
        );
    }

    public Optional<Resource> getVideo() {
        return Optional.ofNullable(video);
    }
}
