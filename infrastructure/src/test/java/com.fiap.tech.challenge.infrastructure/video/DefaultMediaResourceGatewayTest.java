package com.fiap.tech.challenge.infrastructure.video;


import com.fiap.tech.challenge.domain.video.*;
import com.fiap.tech.challenge.infrastructure.services.StorageService;
import com.fiap.tech.challenge.infrastructure.services.local.InMemoryStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static com.fiap.tech.challenge.domain.Fixture.Videos.mediaType;
import static com.fiap.tech.challenge.domain.Fixture.Videos.resource;
import static org.junit.jupiter.api.Assertions.*;


@com.fiap.tech.challenge.infrastructure.IntegrationTest
class DefaultMediaResourceGatewayTest {

    @Autowired
    private StorageService storageService;

    @Autowired
    private MediaResourceGateway mediaResourceGateway;

    @BeforeEach
    void setUp() {
        storageService().reset();
    }

    @Test
    public void testInjection() {
        assertNotNull(mediaResourceGateway);
        assertInstanceOf(DefaultMediaResourceGateway.class, mediaResourceGateway);

        assertNotNull(storageService);
        assertInstanceOf(InMemoryStorageService.class, storageService);
    }

    @Test
    public void givenValidResource_whenCallsStorageAudioVideo_shouldStoreIt() {
        final var expectedVideoId = VideoID.unique();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedClientId = ClientID.unique();
        final var expectedResource = resource(expectedType);
        final var expectedLocation = "clientId-%s/videoId-%s/type-%s".formatted(expectedClientId.getValue(), expectedVideoId.getValue(), expectedType.name());
        final var expectedStatus = MediaStatus.PENDING;
        final var expectedEncodedLocation = "";

        final var actualMedia = this.mediaResourceGateway
                .storeAudioVideo(expectedVideoId, VideoResource.with(expectedResource, expectedType), expectedClientId);

        assertNotNull(actualMedia.id());
        assertEquals(expectedLocation, actualMedia.rawLocation());
        assertEquals(expectedResource.name(), actualMedia.name());
        assertEquals(expectedResource.checksum(), actualMedia.checksum());
        assertEquals(expectedStatus, actualMedia.status());
        assertEquals(expectedEncodedLocation, actualMedia.encodedLocation());

        final var actualStored = storageService().storage().get(expectedLocation);

        assertEquals(expectedResource, actualStored);
    }


    @Test
    public void givenValidVideoId_whenCallsGetResource_shouldReturnIt() {
        final var videoId = VideoID.unique();
        final var expectedClientId = ClientID.unique();
        final var expectedMediaType = VideoMediaType.VIDEO;
        final var expectedResource = resource(expectedMediaType);

        storageService().store("clientId-%s/videoId-%s/type-%s".formatted(expectedClientId.getValue(), videoId.getValue(), VideoMediaType.VIDEO.name()), expectedResource);


        assertEquals(1, storageService().storage().size());

        final var actualResource = this.mediaResourceGateway.getResource(videoId, expectedMediaType, expectedClientId).get();

        assertEquals(expectedResource.name(), actualResource.name());
        assertEquals(expectedResource.checksum(), actualResource.checksum());
        assertEquals(expectedResource.contentType(), actualResource.contentType());
    }


    @Test
    public void givenInvalidVideoId_whenCallsGetResource_shouldReturnEmpty() {
        final var videoId = VideoID.unique();
        final var expectedClientId = ClientID.unique();
        final var expectedMediaType = VideoMediaType.VIDEO;

        storageService().store("clientId-%s/videoId-%s/type-%s".formatted(expectedClientId.getValue(), videoId.getValue(), VideoMediaType.VIDEO.name()), resource(mediaType()));


        assertEquals(1, storageService().storage().size());

        final var actualResource = this.mediaResourceGateway.getResource(VideoID.from("123"), expectedMediaType, expectedClientId);

        assertTrue(actualResource.isEmpty());
    }

    @Test
    public void givenValidVideoId_whenCallsClearResources_shouldDeleteAll() {
        final var videoOne = VideoID.unique();
        final var videoTwo = VideoID.unique();
        final var clientId = ClientID.unique();

        final var toBeDeleted = new ArrayList<String>();
        toBeDeleted.add("clientId-%s/videoId-%s/type-%s".formatted(clientId.getValue(), videoOne.getValue(), VideoMediaType.VIDEO.name()));


        final var expectedValues = new ArrayList<String>();
        expectedValues.add("clientId-%s/videoId-%s/type-%s".formatted(clientId.getValue(), videoTwo.getValue(), VideoMediaType.VIDEO.name()));

        toBeDeleted.forEach(id -> storageService().store(id, resource(mediaType())));

        expectedValues.forEach(id -> storageService().store(id, resource(mediaType())));

        assertEquals(2, storageService().storage().size());

        this.mediaResourceGateway.clearResources(videoOne, clientId);

        assertEquals(1, storageService().storage().size());

        final var keySet = storageService().storage().keySet();

        assertTrue(expectedValues.size() == keySet.size() &&
                expectedValues.containsAll(keySet));
    }

    private InMemoryStorageService storageService() {
        return (InMemoryStorageService) storageService;
    }
}