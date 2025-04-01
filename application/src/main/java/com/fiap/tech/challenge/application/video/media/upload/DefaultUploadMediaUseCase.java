package com.fiap.tech.challenge.application.video.media.upload;

import com.fiap.tech.challenge.domain.video.MediaResourceGateway;
import com.fiap.tech.challenge.domain.video.Video;
import com.fiap.tech.challenge.domain.video.VideoGateway;
import com.fiap.tech.challenge.domain.video.VideoID;

import java.util.Objects;

import static com.fiap.tech.challenge.application.utils.IDNotFoundUtils.notFound;

public class DefaultUploadMediaUseCase extends UploadMediaUseCase {

    private final VideoGateway videoGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public DefaultUploadMediaUseCase(final VideoGateway videoGateway, final MediaResourceGateway mediaResourceGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public UploadMediaOutput execute(final UploadMediaCommand aCommand) {
        final var anId = VideoID.from(aCommand.videoId());
        final var aResource = aCommand.videoResource();

        final var aVideo = videoGateway.findById(anId)
                .orElseThrow(notFound(anId, Video.class));

        switch (aResource.type()) {
            case VIDEO -> aVideo.updateVideoMedia(this.mediaResourceGateway.storeAudioVideo(anId, aResource));
        }

        return UploadMediaOutput.with(videoGateway.update(aVideo), aResource.type());
    }
}
