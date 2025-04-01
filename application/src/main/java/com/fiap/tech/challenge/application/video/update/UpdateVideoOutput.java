package com.fiap.tech.challenge.application.video.update;


import com.fiap.tech.challenge.domain.video.Video;

public record UpdateVideoOutput(String id
) {

    public static UpdateVideoOutput from(final Video aVideo) {
        return new UpdateVideoOutput(aVideo.getId().getValue());
    }
}
