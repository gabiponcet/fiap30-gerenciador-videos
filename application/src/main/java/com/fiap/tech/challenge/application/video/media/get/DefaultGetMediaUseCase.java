package com.fiap.tech.challenge.application.video.media.get;

import com.fiap.tech.challenge.domain.exceptions.NotFoundException;
import com.fiap.tech.challenge.domain.validation.Error;
import com.fiap.tech.challenge.domain.video.MediaResourceGateway;
import com.fiap.tech.challenge.domain.video.VideoID;
import com.fiap.tech.challenge.domain.video.VideoMediaType;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultGetMediaUseCase extends GetMediaUseCase {

    private final MediaResourceGateway mediaResourceGateway;

    public DefaultGetMediaUseCase(final MediaResourceGateway mediaResourceGateway) {
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public MediaOutput execute(final GetMediaCommand aCommand) {
        final var videoID = VideoID.from(aCommand.videoId());
        final var aType = VideoMediaType.of(aCommand.mediaType())
                .orElseThrow(typeNotFound(aCommand.mediaType()));
        return mediaResourceGateway
                .getResource(
                        videoID,
                        aType
                )
                .map(MediaOutput::with)
                .orElseThrow(notFound(videoID.getValue(), aType.name()));
    }

    private Supplier<NotFoundException> typeNotFound(final String aType) {
        return () -> NotFoundException.with(Error.of("Media type %s does not exists".formatted(aType)));

    }

    private Supplier<NotFoundException> notFound(final String anId, final String aType) {
        return () -> NotFoundException.with(Error.of("Resource %s not found for video %s".formatted(aType, anId)));
    }
}
