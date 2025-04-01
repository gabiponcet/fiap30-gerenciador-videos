package com.fiap.tech.challenge.application.video.retrieve.get;

import com.fiap.tech.challenge.domain.video.AudioVideoMedia;
import com.fiap.tech.challenge.domain.video.Video;

public record VideoOutput(
        String id,
        String title,
        String description,
        double duration,
        AudioVideoMedia video
) {
    public static VideoOutput from(final Video aVideo) {
        return new VideoOutput(
                aVideo.getId().getValue(),
                aVideo.getTitle(),
                aVideo.getDescription(),
                aVideo.getDuration(),
                aVideo.getVideo().orElse(null)
        );
    }
}
