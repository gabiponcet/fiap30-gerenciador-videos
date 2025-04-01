package com.fiap.tech.challenge.application.video.retrieve.list;


import com.fiap.tech.challenge.domain.video.Video;
import com.fiap.tech.challenge.domain.video.VideoPreview;

public record VideoListOutput(
        String id,
        String title,
        String description

) {
    public static VideoListOutput from(final VideoPreview aVideo) {
        return new VideoListOutput(
                aVideo.id(),
                aVideo.title(),
                aVideo.description()
        );
    }

    public static VideoListOutput from(final Video aVideo) {
        return new VideoListOutput(
                aVideo.getId().getValue(),
                aVideo.getTitle(),
                aVideo.getDescription()
        );
    }
}
