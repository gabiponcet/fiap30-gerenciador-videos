package com.fiap.tech.challenge.application.video.media.upload;


import com.fiap.tech.challenge.domain.video.Video;
import com.fiap.tech.challenge.domain.video.VideoMediaType;

public record UploadMediaOutput(
        String videoId,
        VideoMediaType mediaType
) {
    public static UploadMediaOutput with(final Video aVideo, final VideoMediaType mediaType) {
        return new UploadMediaOutput(aVideo.getId().getValue(), mediaType);
    }
}
