package com.fiap.tech.challenge.application.video.media.upload;

import com.fiap.tech.challenge.application.UseCaseTest;
import com.fiap.tech.challenge.domain.Fixture;
import com.fiap.tech.challenge.domain.exceptions.NotFoundException;
import com.fiap.tech.challenge.domain.utils.IDUtils;
import com.fiap.tech.challenge.domain.video.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UploadMediaUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUploadMediaUseCase useCase;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private AuthenticatedUser authenticatedUser;

    @Override
    protected List<Object> getMocks() {
        return List.of(mediaResourceGateway, videoGateway, authenticatedUser);
    }

    @Test
    public void givenCommandToUpload_whenIsValid_shouldUpdateVideoMediaAndPersistIt() {
        final var aVideo = Fixture.Videos.systemDesign();

        final var expectedId = aVideo.getId();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = audioVideo(expectedType);
        final var expectedClientId = aVideo.getClientId();

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(aVideo)));
        when(mediaResourceGateway.storeAudioVideo(any(), any(), any())).thenReturn(expectedMedia);
        when(videoGateway.update(any())).thenAnswer(returnsFirstArg());
        when(authenticatedUser.getClientId()).thenReturn(expectedClientId);

        final var aCommand = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(aCommand);

        assertEquals(expectedType, actualOutput.mediaType());
        assertEquals(expectedId.getValue(), actualOutput.videoId());

        verify(videoGateway, times(1)).findById(eq(expectedId));

        verify(mediaResourceGateway, times(1)).storeAudioVideo(eq(expectedId), eq(expectedVideoResource), eq(expectedClientId));

        verify(videoGateway, times(1)).update(argThat(actualVideo ->
                Objects.equals(aVideo.getId(), actualVideo.getId())
                        && Objects.equals(aVideo.getTitle(), actualVideo.getTitle())
                        && Objects.equals(aVideo.getDescription(), actualVideo.getDescription())
                        && Objects.equals(aVideo.getDuration(), actualVideo.getDuration())
                        && Objects.equals(expectedMedia, actualVideo.getVideo().get())
        ));

    }

    @Test
    public void givenCommandToUpload_whenVideoIsInvalid_shouldReturnNotFound() {
        final var aVideo = Fixture.Videos.systemDesign();

        final var expectedId = aVideo.getId();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);

        final var expectedErrorMessage = "Video with ID %s was not found".formatted(expectedId.getValue());

        when(videoGateway.findById(any())).thenReturn(Optional.empty());

        final var aCommand = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualException = assertThrows(NotFoundException.class, () -> useCase.execute(aCommand));

        assertEquals(expectedErrorMessage, actualException.getMessage());

    }

    private AudioVideoMedia audioVideo(final VideoMediaType type) {
        final var checksum = IDUtils.uuid();
        return AudioVideoMedia.with(
                checksum,
                type.name().toLowerCase(),
                "/videos/" + checksum,
                "",
                MediaStatus.PENDING
        );
    }

}
