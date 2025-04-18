package com.fiap.tech.challenge.application.video.retrieve.get;

import com.fiap.tech.challenge.application.UseCaseTest;
import com.fiap.tech.challenge.domain.Fixture;
import com.fiap.tech.challenge.domain.exceptions.NotFoundException;
import com.fiap.tech.challenge.domain.utils.IDUtils;
import com.fiap.tech.challenge.domain.video.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.fiap.tech.challenge.application.utils.IDUtils.asSet;
import static com.fiap.tech.challenge.application.utils.IDUtils.asString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GetVideoByIdUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetVideoByIdUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private AuthenticatedUser authenticatedUser;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway, authenticatedUser);
    }

    @Test
    public void givenAValidExistentId_whenGetVideoById_thenShouldReturnVideo() {
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedDuration = Fixture.duration();
        final var expectedClientId = Fixture.Videos.clientId();


        final var expectedVideo = audioVideo(VideoMediaType.VIDEO);

        final var aVideo = Video.newVideo(
                        expectedTitle,
                        expectedDescription,
                        expectedDuration,
                        expectedClientId
                )
                .updateVideoMedia(expectedVideo);

        final var anId = aVideo.getId();

        when(videoGateway.findById(any())).thenReturn(Optional.of(Video.with(aVideo)));

        final var actualVideo = useCase.execute(anId.getValue());

        assertEquals(anId.getValue(), actualVideo.id());
        assertEquals(expectedTitle, actualVideo.title());
        assertEquals(expectedDescription, actualVideo.description());
        assertEquals(expectedDuration, actualVideo.duration());
        assertEquals(expectedVideo, actualVideo.video());
    }

    @Test
    public void givenInvalidId_whenCallsGetVideo_shouldReturnNotFound() {
        final var expectedErrorMessage = "Video with ID 123 was not found";
        final var expectedId = VideoID.from("123");

        when(videoGateway.findById(any())).thenReturn(Optional.empty());

        final var actualError = assertThrows(NotFoundException.class, () -> useCase.execute(expectedId.getValue()));

        assertEquals(expectedErrorMessage, actualError.getMessage());

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
