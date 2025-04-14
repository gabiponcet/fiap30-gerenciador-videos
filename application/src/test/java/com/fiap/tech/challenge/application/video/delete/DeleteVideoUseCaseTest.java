package com.fiap.tech.challenge.application.video.delete;

import com.fiap.tech.challenge.application.UseCaseTest;
import com.fiap.tech.challenge.domain.exceptions.InternalErrorException;
import com.fiap.tech.challenge.domain.video.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DeleteVideoUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultDeleteVideoUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Mock
    private AuthenticatedUser authenticatedUser;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway, authenticatedUser);
    }

    @Test
    public void givenAValidId_whenCallsDeleteVideo_shouldDelete() {
        final var expectedId = VideoID.unique();

        doNothing().when(videoGateway).deleteById(any());
        doNothing().when(mediaResourceGateway).clearResources(any(), any());
        when(authenticatedUser.getClientId()).thenReturn(ClientID.from("123"));
        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        verify(videoGateway).deleteById(eq(expectedId));
    }

    @Test
    public void givenAValidId_whenCallsDeleteVideoAndGatewayThrowsException_shouldReceiveException() {
        final var expectedId = VideoID.unique();

        doThrow(InternalErrorException.with("Error on delete video", new RuntimeException()))
                .when(videoGateway).deleteById(any());

        assertThrows(InternalErrorException.class, () -> useCase.execute(expectedId.getValue()));

        verify(videoGateway).deleteById(eq(expectedId));
    }

    @Test
    public void givenAnInvalidId_whenCallsDeleteVideo_shouldBeOk() {
        final var expectedId = VideoID.from("123");

        doNothing().when(videoGateway).deleteById(any());
        doNothing().when(mediaResourceGateway).clearResources(any(), any());

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        verify(videoGateway).deleteById(eq(expectedId));
    }
}
