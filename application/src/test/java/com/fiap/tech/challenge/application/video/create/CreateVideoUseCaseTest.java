package com.fiap.tech.challenge.application.video.create;

import com.fiap.tech.challenge.application.UseCaseTest;
import com.fiap.tech.challenge.domain.Fixture;
import com.fiap.tech.challenge.domain.exceptions.InternalErrorException;
import com.fiap.tech.challenge.domain.exceptions.NotificationException;
import com.fiap.tech.challenge.domain.resource.Resource;
import com.fiap.tech.challenge.domain.utils.IDUtils;
import com.fiap.tech.challenge.domain.video.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateVideoUseCaseTest extends UseCaseTest {


    @InjectMocks
    private DefaultCreateVideoUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Mock
    AuthenticatedUser authenticatedUser;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(
                videoGateway,
                mediaResourceGateway,
                authenticatedUser
        );
    }

    @Test
    public void givenAValidCommand_whenCallsCreateVideo_shouldReturnVideoId() {
        final var expectedTitle = "Title";
        final var expectedDescription = "description";
        final var expectedDuration = 120.0;
        final var expectedClientId = Fixture.Videos.clientId();
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);


        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedVideo
        );

        when(authenticatedUser.getClientId()).thenReturn(expectedClientId);
        mockAudioVideoMedia();
        when(videoGateway.create(any())).thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(aCommand);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).create(argThat(actualVideo ->
                Objects.equals(expectedTitle, actualVideo.getTitle())
                        && Objects.equals(expectedDescription, actualVideo.getDescription())
                        && Objects.equals(expectedDuration, actualVideo.getDuration())
                        && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())
        ));

    }

    @Test
    public void givenAValidCommandWithoutResources_whenCallsCreateVideo_shouldReturnVideoId() {
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedClientId = Fixture.Videos.clientId();
        final var expectedDuration = Fixture.duration();

        final Resource expectedVideo = null;

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedDuration,

                expectedVideo
        );

        when(authenticatedUser.getClientId()).thenReturn(expectedClientId);
        when(videoGateway.create(any())).thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(aCommand);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).create(argThat(actualVideo ->
                Objects.equals(expectedTitle, actualVideo.getTitle())
                        && Objects.equals(expectedDescription, actualVideo.getDescription())
                        && Objects.equals(expectedDuration, actualVideo.getDuration())
                        && actualVideo.getVideo().isEmpty()
        ));
    }

    @Test
    public void givenANullTitle_whenCallsCreateVideo_shouldReturnDomainException() {
        final String expectedTitle = null;
        final var expectedDescription = Fixture.Videos.description();
        final var expectedDuration = Fixture.duration();

        final Resource expectedVideo = null;

        final var expectedErrorMessage = "'title' should not be null";
        final var expectedErrorCount = 1;

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedVideo
        );

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        assertEquals(expectedErrorCount, actualException.getErrors().size());
    }

    @Test
    public void givenAEmptyTitle_whenCallsCreateVideo_shouldReturnDomainException() {
        final var expectedTitle = "";
        final var expectedDescription = Fixture.Videos.description();
        final var expectedDuration = Fixture.duration();

        final Resource expectedVideo = null;

        final var expectedErrorMessage = "'title' should not be empty";
        final var expectedErrorCount = 1;

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedVideo
        );


        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        assertEquals(expectedErrorCount, actualException.getErrors().size());
    }

    @Test
    public void givenATitleWithMoreThan255Characters_whenCallsCreateVideo_shouldReturnDomainException() {
        final var expectedTitle = """
                    O vídeo "System Design Interviews Mock" simula uma entrevista técnica sobre design de sistemas, focando em escalabilidade, disponibilidade e performance. 
                    O candidato aborda problemas como projetar um sistema de mensagens em tempo real, explicando decisões arquiteturais. 
                    Diagramas e feedback detalhado ajudam a ilustrar as escolhas. 
                    É uma ótima prática para quem deseja dominar entrevistas de design.
                """;
        ;
        final var expectedDescription = Fixture.Videos.description();
        final var expectedDuration = Fixture.duration();

        final Resource expectedVideo = null;

        final var expectedErrorMessage = "'title' must be between 1 and 255 characters";
        final var expectedErrorCount = 1;

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedVideo
        );


        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        assertEquals(expectedErrorCount, actualException.getErrors().size());
    }


    @Test
    public void givenAValidCommand_whenCallsCreateVideoThrowsException_shouldReturnCallClearResources() {
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedClientId = Fixture.Videos.clientId();
        final var expectedDuration = Fixture.duration();

        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);

        final var expectedErrorMessage = "An error on create video was observed [videoId:";

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedVideo
        );

        mockAudioVideoMedia();
        when(videoGateway.create(any())).thenThrow(new RuntimeException("Internal Server Error"));
        when(authenticatedUser.getClientId()).thenReturn(expectedClientId);

        final var actualException = assertThrows(InternalErrorException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertTrue(actualException.getMessage().startsWith(expectedErrorMessage));

        verify(mediaResourceGateway).clearResources(any(), any());
    }

    private void mockAudioVideoMedia() {
        when(mediaResourceGateway.storeAudioVideo(any(), any(), any())).thenAnswer(t -> {
            final var resource = t.getArgument(1, VideoResource.class);
            return AudioVideoMedia.with(IDUtils.uuid(), resource.resource().name(), "/video", "", MediaStatus.PENDING);
        });
    }


}