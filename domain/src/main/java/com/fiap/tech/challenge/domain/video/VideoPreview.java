package com.fiap.tech.challenge.domain.video;

import java.time.Instant;

public record VideoPreview(
        String id,
        String title,
        String description
) {
    public static VideoPreview from(final Video video) {
        return new VideoPreview(
                video.getId().getValue(),
                video.getTitle(),
                video.getDescription()
        );
    }
}
