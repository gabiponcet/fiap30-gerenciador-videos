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
    public AudioVideoMedia storeAudioVideo(final VideoID anId, final VideoResource videoResource) {
        final var filepath = filepath(anId, videoResource);
        final var aResource = videoResource.resource();
        store(filepath, aResource);
        return AudioVideoMedia.with(aResource.checksum(), aResource.name(), filepath);
    }

    @Override
    public Optional<Resource> getResource(final VideoID anId, final VideoMediaType type) {
        final var filepath = filepath(anId, type);
        return this.storageService.get(filepath);
    }

    @Override
    public void clearResources(final VideoID anId) {
        final var ids = this.storageService.list(folder(anId));
        this.storageService.deleteAll(ids);
    }

    private String filename(final VideoMediaType type) {
        return filenamePattern.replace("{type}", type.name());
    }

    private String folder(final VideoID anId) {
        return locationPattern.replace("{videoId}", anId.getValue());
    }

    private String filepath(final VideoID anId, final VideoResource aResource) {
        return filepath(anId, aResource.type());
    }

    private String filepath(final VideoID anId, final VideoMediaType type) {
        return folder(anId) + "/" + filename(type);
    }

    private void store(final String filepath, final Resource aResource) {
        storageService.store(filepath, aResource);
    }
}
