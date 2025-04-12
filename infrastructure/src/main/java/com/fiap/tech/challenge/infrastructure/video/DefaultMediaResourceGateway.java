package com.fiap.tech.challenge.infrastructure.video;

import com.fiap.tech.challenge.domain.resource.Resource;
import com.fiap.tech.challenge.domain.video.*;
import com.fiap.tech.challenge.infrastructure.configuration.properties.storage.StorageProperties;
import com.fiap.tech.challenge.infrastructure.services.StorageService;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultMediaResourceGateway implements MediaResourceGateway {

    private final String filenamePattern;
    private final String locationPattern;
    private final StorageService storageService;

    public DefaultMediaResourceGateway(
            final StorageProperties props,
            final StorageService storageService
    ) {
        this.filenamePattern = props.getFileNamePattern();
        this.locationPattern = props.getLocationPattern();
        this.storageService = storageService;
    }

    @Override
    public AudioVideoMedia storeAudioVideo(final VideoID anId, final VideoResource videoResource, final ClientID clientId) {
        final var filepath = filepath(anId, videoResource, clientId.getValue());
        final var aResource = videoResource.resource();
        store(filepath, aResource);
        return AudioVideoMedia.with(aResource.checksum(), aResource.name(), filepath);
    }

    @Override
    public Optional<Resource> getResource(final VideoID anId, final VideoMediaType type, final ClientID clientId) {
        final var filepath = filepath(anId, type, clientId.getValue());
        return this.storageService.get(filepath);
    }

    @Override
    public void clearResources(final VideoID anId, final ClientID clientId) {
        final var ids = this.storageService.list(folder(anId, clientId.getValue()));
        this.storageService.deleteAll(ids);
    }

    private String filename(final VideoMediaType type) {
        return filenamePattern.replace("{type}", type.name());
    }

    private String folder(final VideoID anId, final String clientId) {
        return locationPattern
                .replace("{videoId}", anId.getValue())
                .replace("{clientId}", clientId);
    }

    private String filepath(final VideoID anId, final VideoResource aResource, final String clientId) {
        return folder(anId, clientId) + "/" + filename(aResource.type());
    }

    private String filepath(final VideoID anId, final VideoMediaType type, final String clientId) {
        return folder(anId, clientId) + "/" + filename(type);
    }

    private void store(final String filepath, final Resource aResource) {
        storageService.store(filepath, aResource);
    }
}
