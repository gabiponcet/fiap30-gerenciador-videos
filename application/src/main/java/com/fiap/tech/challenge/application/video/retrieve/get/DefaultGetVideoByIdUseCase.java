package com.fiap.tech.challenge.application.video.retrieve.get;


import com.fiap.tech.challenge.domain.video.Video;
import com.fiap.tech.challenge.domain.video.VideoGateway;
import com.fiap.tech.challenge.domain.video.VideoID;

import java.util.Objects;

import static com.fiap.tech.challenge.application.utils.IDNotFoundUtils.notFound;


public class DefaultGetVideoByIdUseCase extends GetVideoByIdUseCase {

    private final VideoGateway videoGateway;

    public DefaultGetVideoByIdUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public VideoOutput execute(final String anId) {
        final var id = VideoID.from(anId);
        return videoGateway.findById(id).map(VideoOutput::from).orElseThrow(notFound(id, Video.class));
    }
}
