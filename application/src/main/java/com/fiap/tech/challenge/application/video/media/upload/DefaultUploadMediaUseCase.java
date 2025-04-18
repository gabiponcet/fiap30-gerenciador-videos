package com.fiap.tech.challenge.application.video.media.upload;

import com.fiap.tech.challenge.domain.video.*;

import java.util.Objects;

import static com.fiap.tech.challenge.application.utils.IDNotFoundUtils.notFound;

public class DefaultUploadMediaUseCase extends UploadMediaUseCase {

    private final VideoGateway videoGateway;
    private final MediaResourceGateway mediaResourceGateway;
    private final AuthenticatedUser authenticatedUser;

    public DefaultUploadMediaUseCase(final VideoGateway videoGateway,
                                     final MediaResourceGateway mediaResourceGateway,
                                     final AuthenticatedUser authenticatedUser) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public UploadMediaOutput execute(final UploadMediaCommand aCommand) {
        final var anId = VideoID.from(aCommand.videoId());
        final var aResource = aCommand.videoResource();

        final var aVideo = videoGateway.findById(anId)
                .orElseThrow(notFound(anId, Video.class));

        final var currentClientId = authenticatedUser.getClientId();

        if (!currentClientId.equals(aVideo.getClientId())) {
            throw new SecurityException("Access denied to video " + aVideo.getId().getValue());
        }

        switch (aResource.type()) {
            case VIDEO -> aVideo.updateVideoMedia(
                    this.mediaResourceGateway.storeAudioVideo(anId, aResource, currentClientId)
            );
        }

        return UploadMediaOutput.with(videoGateway.update(aVideo), aResource.type());
    }

}
