package com.fiap.tech.challenge.application.video.create;

import com.fiap.tech.challenge.domain.exceptions.InternalErrorException;
import com.fiap.tech.challenge.domain.exceptions.NotificationException;
import com.fiap.tech.challenge.domain.validation.handler.Notification;
import com.fiap.tech.challenge.domain.video.*;

import java.util.Objects;

public class DefaultCreateVideoUseCase
        extends CreateVideoUseCase {

    private final VideoGateway videoGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public DefaultCreateVideoUseCase(
            final VideoGateway videoGateway,
            final MediaResourceGateway mediaResourceGateway
    ) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }


    @Override
    public CreateVideoOutput execute(final CreateVideoCommand aCommand) {
        final var aTitle = aCommand.title();
        final var aDescription = aCommand.description();
        final var aDuration = aCommand.duration();

        final var notification = Notification.create();

        final var aVideo = Video.newVideo(aTitle, aDescription, aDuration);

        aVideo.validate(notification);

        if(notification.hasErrors()) {
            throw new NotificationException("Could not create Aggregate Video", notification);
        }

        return CreateVideoOutput.from(create(aCommand, aVideo));
    }

    private Video create(final CreateVideoCommand aCommand, final Video aVideo) {
        final var anId = aVideo.getId();

        try {
            final var aVideoMedia = aCommand.getVideo()
                    .map(it -> mediaResourceGateway.storeAudioVideo(anId, VideoResource.with(it, VideoMediaType.VIDEO)))
                    .orElse(null);

            return videoGateway.create(
                    aVideo
                            .updateVideoMedia(aVideoMedia)
            );
        } catch (Throwable t) {
            mediaResourceGateway.clearResources(anId);
            throw InternalErrorException.with("An error on create video was observed [videoId:%s]".formatted(anId.getValue()), t);
        }
    }

}
