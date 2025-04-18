package com.fiap.tech.challenge.infrastructure.video.presenters;

import com.fiap.tech.challenge.application.video.media.upload.UploadMediaOutput;
import com.fiap.tech.challenge.application.video.retrieve.get.VideoOutput;
import com.fiap.tech.challenge.application.video.retrieve.list.VideoListOutput;
import com.fiap.tech.challenge.domain.video.AudioVideoMedia;
import com.fiap.tech.challenge.infrastructure.video.models.*;

public interface VideoApiPresenter {

    static UploadMediaResponse present(final UploadMediaOutput output) {
        return new UploadMediaResponse(
                output.videoId(),
                output.mediaType().name()
        );
    }

    static VideoListResponse present(final VideoListOutput output) {
        return new VideoListResponse(
                output.id(),
                output.title(),
                output.description()
        );
    }

    static VideoResponse present(final VideoOutput output) {
        return new VideoResponse(
                output.id(),
                output.title(),
                output.description(),
                output.duration(),
                present(output.video())
        );
    }


    static AudioVideoMediaResponse present(final AudioVideoMedia audioVideo) {
        if (audioVideo == null) {
            return null;
        }
        return new AudioVideoMediaResponse(
                audioVideo.id(),
                audioVideo.checksum(),
                audioVideo.name(),
                audioVideo.rawLocation(),
                audioVideo.encodedLocation(),
                audioVideo.status().name()
        );
    }
}
