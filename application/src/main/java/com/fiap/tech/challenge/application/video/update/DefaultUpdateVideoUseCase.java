package com.fiap.tech.challenge.application.video.update;

import com.fiap.tech.challenge.domain.exceptions.InternalErrorException;
import com.fiap.tech.challenge.domain.exceptions.NotificationException;
import com.fiap.tech.challenge.domain.validation.handler.Notification;
import com.fiap.tech.challenge.domain.video.*;

import java.util.Objects;

import static com.fiap.tech.challenge.application.utils.IDNotFoundUtils.notFound;

public class DefaultUpdateVideoUseCase extends UpdateVideoUseCase {

    private final VideoGateway videoGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public DefaultUpdateVideoUseCase(
            final VideoGateway videoGateway,
            final MediaResourceGateway mediaResourceGateway
    ) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public UpdateVideoOutput execute(final UpdateVideoCommand aCommand) {
        final var anId = VideoID.from(aCommand.id());

        final var aTitle = aCommand.title();
        final var aDescription = aCommand.description();
        final var aDuration = aCommand.duration();

        final var aVideo = videoGateway.findById(anId).orElseThrow(notFound(anId, Video.class));

        final var notification = Notification.create();


        aVideo.update(
                aTitle,
                aDescription,
                aDuration
        );

        aVideo.validate(notification);

        if (notification.hasErrors()) {
            throw new NotificationException("Could not update Aggregate Video with ID %s".formatted(anId.getValue()), notification);
        }

        return UpdateVideoOutput.from(update(aCommand, aVideo));
    }

    private Video update(final UpdateVideoCommand aCommand, final Video aVideo) {
        final var anId = aVideo.getId();

        try {
            final var aVideoMedia = aCommand.getVideo()
                    .map(it -> mediaResourceGateway.storeAudioVideo(anId, VideoResource.with(it, VideoMediaType.VIDEO)))
                    .orElse(null);

            return videoGateway.update(
                    aVideo
                            .updateVideoMedia(aVideoMedia)
            );
        } catch (Throwable t) {
            throw InternalErrorException.with("An error on updating video was observed [videoId:%s]".formatted(anId.getValue()), t);
        }
    }
}
