package com.fiap.tech.challenge.application.video.media.get;

import com.fiap.tech.challenge.application.UseCaseTest;
import com.fiap.tech.challenge.domain.Fixture;
import com.fiap.tech.challenge.domain.exceptions.NotFoundException;
import com.fiap.tech.challenge.domain.video.AuthenticatedUser;
import com.fiap.tech.challenge.domain.video.MediaResourceGateway;
import com.fiap.tech.challenge.domain.video.VideoID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class GetMediaUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetMediaUseCase useCase;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Mock
    private AuthenticatedUser authenticatedUser;

    @Override
    protected List<Object> getMocks() {
        return List.of(mediaResourceGateway, authenticatedUser);
    }

    @Test
    public void givenVideoIdAndType_whenIsValidCmd_shouldReturnResource() {
        final var expectedId = VideoID.unique();
        final var expectedType = Fixture.Videos.mediaType();
        final var expectedClientId = Fixture.Videos.clientId();
        final var expectedResource = Fixture.Videos.resource(expectedType);

        when(mediaResourceGateway.getResource(expectedId, expectedType, expectedClientId)).thenReturn(Optional.of(expectedResource));
        when(authenticatedUser.getClientId()).thenReturn(expectedClientId);

        final var aCmd = GetMediaCommand.with(expectedId.getValue(), expectedType.name());

        final var actualResult = this.useCase.execute(aCmd);

        assertEquals(expectedResource.name(), actualResult.name());
        assertEquals(expectedResource.contentType(), actualResult.contentType());
        assertEquals(expectedResource.content(), actualResult.content());
    }

    @Test
    public void givenVideoIdAndType_whenIsNotFound_shouldReturnNotFoundException() {
        final var expectedId = VideoID.unique();
        final var expectedType = Fixture.Videos.mediaType();
        final var expectedErrorMessage = "Resource %s not found for video %s"
                .formatted(expectedType.name(), expectedId.getValue());
        final var expectedClientId = Fixture.Videos.clientId();

        when(mediaResourceGateway.getResource(expectedId, expectedType, expectedClientId)).thenReturn(Optional.empty());
        when(authenticatedUser.getClientId()).thenReturn(expectedClientId);

        final var aCmd = GetMediaCommand.with(expectedId.getValue(), expectedType.name());

        final var actualException = assertThrows(NotFoundException.class, () -> this.useCase.execute(aCmd));

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    public void givenVideoIdAndType_whenTypeDoesNotExists_shouldReturnNotFoundException() {
        final var expectedId = VideoID.unique();
        final var expectedType = "Nonexistent media type";
        final var expectedErrorMessage = "Media type %s does not exists"
                .formatted(expectedType);

        final var aCmd = GetMediaCommand.with(expectedId.getValue(), expectedType);

        final var actualException = assertThrows(NotFoundException.class, () -> this.useCase.execute(aCmd));

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }
}
