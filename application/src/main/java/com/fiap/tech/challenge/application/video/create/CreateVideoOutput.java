package com.fiap.tech.challenge.application.video.create;


import com.fiap.tech.challenge.domain.video.Video;

public record CreateVideoOutput(
        String id
) {

    public static CreateVideoOutput from(final Video aVideo) {
        return new CreateVideoOutput(aVideo.getId().getValue());
    }
}
