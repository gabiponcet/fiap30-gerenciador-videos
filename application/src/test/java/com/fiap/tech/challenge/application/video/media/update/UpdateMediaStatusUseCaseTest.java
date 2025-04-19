package com.fiap.tech.challenge.application.video.media.update;

import com.fiap.tech.challenge.application.UseCaseTest;
import com.fiap.tech.challenge.domain.Fixture;
import com.fiap.tech.challenge.domain.video.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UpdateMediaStatusUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUpdateMediaStatusUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private AuthenticatedUser authenticatedUser;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway, authenticatedUser);
    }

    @Test
    public void givenACommandForVideo_whenIsValid_shouldUpdateStatusAndEncodedLocation() {
        final var expectedStatus = MediaStatus.COMPLETED;
        final var expectedFolder = "encoded_media";
        final var expectedFilename = "filename.mp4";
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedMedia = Fixture.Videos.audioVideo(expectedType);

        final var aVideo = Fixture.Videos.systemDesign().updateVideoMedia(expectedMedia);

        final var expectedId = aVideo.getId();

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(aVideo)));
        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var aCommand = UpdateMediaStatusCommand.with(
                expectedStatus,
                expectedId.getValue(),
                expectedMedia.id(),
                expectedFolder,
                expectedFilename
        );

        this.useCase.execute(aCommand);

        verify(videoGateway, times(1)).findById(eq(expectedId));

        final var captor = ArgumentCaptor.forClass(Video.class);

        verify(videoGateway, times(1)).update(captor.capture());

        final var actualVideo = captor.getValue();

        final var actualVideoMedia = actualVideo.getVideo().get();

        assertEquals(expectedMedia.id(), actualVideoMedia.id());
        assertEquals(expectedMedia.rawLocation(), actualVideoMedia.rawLocation());
        assertEquals(expectedMedia.checksum(), actualVideoMedia.checksum());
        assertEquals(expectedStatus, actualVideoMedia.status());
        assertEquals(expectedFolder.concat("/").concat(expectedFilename), actualVideoMedia.encodedLocation());
    }

    @Test
    public void givenACommandForVideo_whenIsValidForProcessing_shouldUpdateStatusAndEncodedLocation() {
        final var expectedStatus = MediaStatus.PROCESSING;
        final String expectedFolder = null;
        final String expectedFilename = null;
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedMedia = Fixture.Videos.audioVideo(expectedType);

        final var aVideo = Fixture.Videos.systemDesign().updateVideoMedia(expectedMedia);

        final var expectedId = aVideo.getId();

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(aVideo)));
        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());

        final var aCommand = UpdateMediaStatusCommand.with(
                expectedStatus,
                expectedId.getValue(),
                expectedMedia.id(),
                expectedFolder,
                expectedFilename
        );

        this.useCase.execute(aCommand);

        verify(videoGateway, times(1)).findById(eq(expectedId));

        final var captor = ArgumentCaptor.forClass(Video.class);

        verify(videoGateway, times(1)).update(captor.capture());

        final var actualVideo = captor.getValue();


        final var actualVideoMedia = actualVideo.getVideo().get();

        assertEquals(expectedMedia.id(), actualVideoMedia.id());
        assertEquals(expectedMedia.rawLocation(), actualVideoMedia.rawLocation());
        assertEquals(expectedMedia.checksum(), actualVideoMedia.checksum());
        assertEquals(expectedStatus, actualVideoMedia.status());
        assertTrue(actualVideoMedia.encodedLocation().isBlank());
    }

}
