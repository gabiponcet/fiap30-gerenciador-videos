package com.fiap.tech.challenge.application.video.delete;

import com.fiap.tech.challenge.domain.video.AuthenticatedUser;
import com.fiap.tech.challenge.domain.video.MediaResourceGateway;
import com.fiap.tech.challenge.domain.video.VideoGateway;
import com.fiap.tech.challenge.domain.video.VideoID;

import java.util.Objects;

public class DefaultDeleteVideoUseCase extends DeleteVideoUseCase {

    private final VideoGateway videoGateway;
    private final MediaResourceGateway mediaResourceGateway;
    private final AuthenticatedUser authenticatedUser;

    public DefaultDeleteVideoUseCase(final VideoGateway videoGateway,
                                     final MediaResourceGateway mediaResourceGateway,
                                     final AuthenticatedUser authenticatedUser) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.authenticatedUser = Objects.requireNonNull(authenticatedUser);
    }

    @Override
    public void execute(final String anId) {
        final var videoID = VideoID.from(anId);
        final var clientId = authenticatedUser.getClientId();
        mediaResourceGateway.clearResources(videoID, clientId);
        videoGateway.deleteById(videoID);
    }
}
