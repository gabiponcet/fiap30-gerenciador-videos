package com.fiap.tech.challenge.domain.video;

import com.fiap.tech.challenge.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

public class VideoTest extends com.fiap.tech.challenge.domain.UnitTest {

    @Test
    public void givenValidParams_whenCallsNewVideo_shouldInstantiate() {
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = """
                    O vídeo "System Design Interviews Mock" simula uma entrevista técnica sobre design de sistemas, focando em escalabilidade, disponibilidade e performance. 
                    O candidato aborda problemas como projetar um sistema de mensagens em tempo real, explicando decisões arquiteturais. 
                    Diagramas e feedback detalhado ajudam a ilustrar as escolhas. 
                    É uma ótima prática para quem deseja dominar entrevistas de design.
                """;
        final var expectedDuration = 120.0;
        final var expectedClientId = ClientID.unique();


        final var actualVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedClientId
        );

        assertNotNull(actualVideo);
        assertNotNull(actualVideo.getId());
        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedClientId, actualVideo.getClientId());
        assertTrue(actualVideo.getVideo().isEmpty());

        assertTrue(actualVideo.getDomainEvents().isEmpty());

        assertDoesNotThrow(() -> actualVideo.validate(new ThrowsValidationHandler()));
    }

    @Test
    public void givenValidVideo_whenCallsUpdate_shouldReturnUpdated() throws Exception {
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = """
                    O vídeo "System Design Interviews Mock" simula uma entrevista técnica sobre design de sistemas, focando em escalabilidade, disponibilidade e performance. 
                    O candidato aborda problemas como projetar um sistema de mensagens em tempo real, explicando decisões arquiteturais. 
                    Diagramas e feedback detalhado ajudam a ilustrar as escolhas. 
                    É uma ótima prática para quem deseja dominar entrevistas de design.
                """;
        final var expectedDuration = 120.0;
        final var expectedClientId = ClientID.unique();
        final var expectedEvent = new VideoMediaCreated("ID", "file");
        final var expectedEventCount = 1;

        final var aVideo = Video.newVideo(
                "Test title",
                "Lalala description",
                0.0,
                ClientID.from("123")

        );

        aVideo.registerEvent(expectedEvent);

        final var actualVideo = aVideo.with(aVideo).update(
                expectedTitle,
                expectedDescription,
                expectedDuration
        );

        assertNotNull(actualVideo);
        assertNotNull(actualVideo.getId());
        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertTrue(actualVideo.getVideo().isEmpty());
        assertEquals(expectedEventCount, actualVideo.getDomainEvents().size());
        assertEquals(expectedEvent, actualVideo.getDomainEvents().get(0));

        assertDoesNotThrow(() -> actualVideo.validate(new ThrowsValidationHandler()));
    }

    @Test
    public void givenValidVideo_whenCallsUpdateVideoMedia_shouldReturnUpdated() throws Exception {
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = """
                    O vídeo "System Design Interviews Mock" simula uma entrevista técnica sobre design de sistemas, focando em escalabilidade, disponibilidade e performance. 
                    O candidato aborda problemas como projetar um sistema de mensagens em tempo real, explicando decisões arquiteturais. 
                    Diagramas e feedback detalhado ajudam a ilustrar as escolhas. 
                    É uma ótima prática para quem deseja dominar entrevistas de design.
                """;
        final var expectedLaunchedAt = Year.of(2017);
        final var expectedDuration = 120.0;
        final var expectedClientId = ClientID.unique();


        final var aVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedClientId
        );
        final var aVideoMedia =
                AudioVideoMedia.with("abc", "Video.mp4", "/123/videos", "", MediaStatus.PENDING);

        final var expectedDomainEventsSize = 1;
        final var expectedDomainEvents = new VideoMediaCreated(aVideoMedia.id(), aVideoMedia.rawLocation());

        final var actualVideo = aVideo.with(aVideo).updateVideoMedia(aVideoMedia);

        assertNotNull(actualVideo);
        assertNotNull(actualVideo.getId());
        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedClientId, actualVideo.getClientId());

        assertEquals(aVideoMedia, actualVideo.getVideo().get());
        assertEquals(expectedDomainEventsSize, actualVideo.getDomainEvents().size());
        final var actualEvent = (VideoMediaCreated) actualVideo.getDomainEvents().get(0);
        assertEquals(aVideo.getId().getValue(), actualEvent.resourceId());
        assertEquals(aVideoMedia.rawLocation(), actualEvent.filePath());

        assertDoesNotThrow(() -> actualVideo.validate(new ThrowsValidationHandler()));
    }


    @Test
    public void givenValidVideo_whenCallsWith_shouldCreateWithoutEvents() throws Exception {
        final var expectedId = VideoID.unique();
        final var expectedTitle = "System Design Interviews";
        final var expectedDescription = """
                    O vídeo "System Design Interviews Mock" simula uma entrevista técnica sobre design de sistemas, focando em escalabilidade, disponibilidade e performance. 
                    O candidato aborda problemas como projetar um sistema de mensagens em tempo real, explicando decisões arquiteturais. 
                    Diagramas e feedback detalhado ajudam a ilustrar as escolhas. 
                    É uma ótima prática para quem deseja dominar entrevistas de design.
                """;
        final var expectedDuration = 120.0;
        final var expectedClientId = ClientID.unique();
        final var expectedEventCount = 0;


        final var actualVideo = Video.with(
                expectedId,
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedClientId,
                null

        );

        assertNotNull(actualVideo);
        assertEquals(expectedId, actualVideo.getId());

        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedClientId, actualVideo.getClientId());

        assertTrue(actualVideo.getVideo().isEmpty());

        assertEquals(expectedEventCount, actualVideo.getDomainEvents().size());

        assertDoesNotThrow(() -> actualVideo.validate(new ThrowsValidationHandler()));
    }
}
