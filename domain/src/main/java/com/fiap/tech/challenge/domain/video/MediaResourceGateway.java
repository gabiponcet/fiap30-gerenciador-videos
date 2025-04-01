package com.fiap.tech.challenge.domain.video;


import com.fiap.tech.challenge.domain.resource.Resource;

import java.util.Optional;

public interface MediaResourceGateway {

    AudioVideoMedia storeAudioVideo(VideoID anId, VideoResource aResource);

    Optional<Resource> getResource (VideoID anId, VideoMediaType type);

    void clearResources(VideoID anId);
}
