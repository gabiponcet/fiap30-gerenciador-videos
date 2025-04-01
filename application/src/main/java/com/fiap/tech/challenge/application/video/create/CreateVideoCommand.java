package com.fiap.tech.challenge.application.video.create;


import com.fiap.tech.challenge.domain.resource.Resource;

import java.util.Optional;

public record CreateVideoCommand(
        String title,
        String description,
        Double duration,
        Resource video
) {

    public static CreateVideoCommand with(
            final String title,
            final String description,
            final Double duration,
            final Resource video
    ) {
        return new CreateVideoCommand(
                title,
                description,
                duration,
                video
        );
    }

    public Optional<Resource> getVideo() {
        return Optional.ofNullable(video);
    }
}
