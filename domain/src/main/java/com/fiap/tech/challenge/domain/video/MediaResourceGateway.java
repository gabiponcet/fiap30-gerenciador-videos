package com.fiap.tech.challenge.domain.video;


import com.fiap.tech.challenge.domain.resource.Resource;

import java.util.Optional;

public interface MediaResourceGateway {

    AudioVideoMedia storeAudioVideo(VideoID anId, VideoResource videoResource, ClientID clientId);

    Optional<Resource> getResource(VideoID anId, VideoMediaType type, ClientID clientId);

    void clearResources(VideoID anId, ClientID clientId);
}
