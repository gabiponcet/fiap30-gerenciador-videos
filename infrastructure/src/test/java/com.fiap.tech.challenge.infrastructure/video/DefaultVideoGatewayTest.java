package com.fiap.tech.challenge.infrastructure.video;

import com.fiap.tech.challenge.domain.Fixture;
import com.fiap.tech.challenge.domain.pagination.SearchQuery;
import com.fiap.tech.challenge.domain.utils.IDUtils;
import com.fiap.tech.challenge.domain.video.*;
import com.fiap.tech.challenge.infrastructure.IntegrationTest;
import com.fiap.tech.challenge.infrastructure.video.persistence.VideoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.wildfly.common.Assert.assertFalse;
import static org.wildfly.common.Assert.assertTrue;

@IntegrationTest
class DefaultVideoGatewayTest {

    @Autowired
    private DefaultVideoGateway videoGateway;

    @Autowired
    private VideoRepository videoRepository;

    private static final ClientID clientId = ClientID.from("clientId");

    @Test
    void testInjection() {
        assertNotNull(videoGateway);
    }

    @BeforeEach
    public void setUp() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn(clientId.getValue());

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @Transactional
    void givenAValidVideo_whenCallsCreate_shouldPersistIt() {
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedDuration = Fixture.duration();
        final var expectedClientId = Fixture.video().getClientId();


        final var expectedVideo = audioVideo(VideoMediaType.VIDEO);


        final var aVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                        expectedClientId
        )
                .updateVideoMedia(expectedVideo);


        final var actualVideo = videoGateway.create(
                aVideo
        );

        assertNotNull(actualVideo);
        assertNotNull(actualVideo.getId());


        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedVideo.name(), actualVideo.getVideo().get().name());

        final var persistedVideo = videoRepository.findById(actualVideo.getId().getValue()).get();

        assertEquals(expectedTitle, persistedVideo.getTitle());
        assertEquals(expectedDescription, persistedVideo.getDescription());
        assertEquals(expectedDuration, persistedVideo.getDuration());

        assertEquals(expectedVideo.name(), persistedVideo.getVideo().toDomain().name());
    }

    @Test
    @Transactional
    void givenAValidVideoWithoutRelations_whenCallsCreate_shouldPersistIt() {

        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedDuration = Fixture.duration();
        final var expectedClientId = Fixture.video().getClientId();


        final var aVideo = Video.newVideo(
                        expectedTitle,
                        expectedDescription,
                        expectedDuration,
                        expectedClientId
                );


        final var actualVideo = videoGateway.create(
                aVideo
        );

        assertNotNull(actualVideo);
        assertNotNull(actualVideo.getId());


        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertTrue(actualVideo.getVideo().isEmpty());

        final var persistedVideo = videoRepository.findById(actualVideo.getId().getValue()).get();

        assertEquals(expectedTitle, persistedVideo.getTitle());
        assertEquals(expectedDescription, persistedVideo.getDescription());
        assertEquals(expectedDuration, persistedVideo.getDuration());
        Assertions.assertNull(persistedVideo.getVideo());
    }

    @Test
    @Transactional
    void givenAValidVideo_whenCallsUpdate_shouldPersistIt() {
        final var clientId = Fixture.video().getClientId();
        final var video = videoGateway.create(
                Video.newVideo(
                        Fixture.title(),
                        Fixture.Videos.description(),
                        Fixture.duration(),
                        clientId
                )
        );

        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedDuration = Fixture.duration();

        final AudioVideoMedia expectedVideo = audioVideo(VideoMediaType.VIDEO);


        final var aVideo = Video.with(video).update(
                        expectedTitle,
                        expectedDescription,
                        expectedDuration
                )
                .updateVideoMedia(expectedVideo);


        final var actualVideo = videoGateway.update(
                aVideo
        );

        assertNotNull(actualVideo);
        assertNotNull(actualVideo.getId());


        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedVideo.name(), actualVideo.getVideo().get().name());

        final var persistedVideo = videoRepository.findById(actualVideo.getId().getValue()).get();

        assertEquals(expectedTitle, persistedVideo.getTitle());
        assertEquals(expectedDescription, persistedVideo.getDescription());
        assertEquals(expectedDuration, persistedVideo.getDuration());
        assertEquals(expectedVideo.name(), persistedVideo.getVideo().toDomain().name());
    }

    @Test
    void givenAValidVideoId_whenCallsDeleteById_shouldDeleteIt() {
        final var video = videoGateway.create(
                Video.newVideo(
                        Fixture.title(),
                        Fixture.Videos.description(),
                        Fixture.duration(),
                        clientId

                )
        );

        final var anId = video.getId();

        assertEquals(1, videoRepository.count());

        videoGateway.deleteById(
                anId
        );

        assertEquals(0, videoRepository.count());

    }

    @Test
    void givenAInvalidVideoId_whenCallsDeleteById_shouldDeleteIt() {
        videoGateway.create(
                Video.newVideo(
                        Fixture.title(),
                        Fixture.Videos.description(),
                        Fixture.duration(),
                        clientId

                )
        );

        final var anId = VideoID.unique();

        assertEquals(1, videoRepository.count());

        videoGateway.deleteById(
                anId
        );

        assertEquals(1, videoRepository.count());

    }

    @Test
    void givenAValidVideo_whenCallsFindById_shouldReturnIt() {
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedDuration = Fixture.duration();
        final var expectedClientId = clientId;
        final var expectedVideo = audioVideo(VideoMediaType.VIDEO);



        final var aVideo = videoGateway.create(Video.newVideo(
                        expectedTitle,
                        expectedDescription,
                        expectedDuration,
                        clientId
                )
                .updateVideoMedia(expectedVideo)
        );

        final var anId = aVideo.getId();

        final var actualVideo = videoGateway.findById(
                anId
        ).get();

        assertNotNull(actualVideo);
        assertNotNull(actualVideo.getId());

        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedVideo.name(), actualVideo.getVideo().get().name());
    }

    @Test
    void givenAllParams_whenCallsFindAll_shouldReturnFilteredList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 4;

        final var aQuery =
                new SearchQuery(
                        expectedPage,
                        expectedPerPage,
                        expectedTerms,
                        expectedSort,
                        expectedDirection
                );

        final var actualPage = videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());
        assertEquals("21.1 Implementação dos testes integrados do findAll", actualPage.items().get(0).title());
    }

    @Test
    void givenEmptyParams_whenCallsFindAll_shouldReturnAllList() {
        mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 4;

        final var aQuery =
                new SearchQuery(
                        expectedPage,
                        expectedPerPage,
                        expectedTerms,
                        expectedSort,
                        expectedDirection
                );

        final var actualPage = videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());
    }

    @Test
    public void givenEmptyVideos_whenCallsFindAll_shouldReturnEmptyList() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var aQuery =
                new SearchQuery(
                        expectedPage,
                        expectedPerPage,
                        expectedTerms,
                        expectedSort,
                        expectedDirection
                );

        final var actualPage = videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());
    }

    @ParameterizedTest
    @CsvSource({
            "title,asc,0,10,4,4,21.1 Implementação dos testes integrados do findAll",
            "title,desc,0,10,4,4,System Design",
            "title,asc,0,10,4,4,21.1 Implementação dos testes integrados do findAll",
            "title,desc,0,10,4,4,System Design",
    })
    public void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnOrdered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedVideoTitle
    ) {
        // given
        final var expectedTerms = "";

        mockVideos();

        final var aQuery =
                new SearchQuery(
                        expectedPage,
                        expectedPerPage,
                        expectedTerms,
                        expectedSort,
                        expectedDirection
                );

        // when
        final var actualPage = videoGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());
        Assertions.assertEquals(expectedVideoTitle, actualPage.items().get(0).title());
    }

    @ParameterizedTest
    @CsvSource({
            "yste,0,10,1,1,System Design",
            "eta ,0,10,1,1,Não cometa esse erro",
            "stes inte,0,10,1,1,21.1 Implementação dos testes integrados do findAll",
            "e empree,0,10,1,1,Aula de empreendedorismo"
    })
    public void givenAValidTerm_whenCallsFindAll_shouldReturnFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedVideoTitle
    ) {

        mockVideos();
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery =
                new SearchQuery(
                        expectedPage,
                        expectedPerPage,
                        expectedTerms,
                        expectedSort,
                        expectedDirection
                );

        final var actualPage = videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());
        assertEquals(expectedVideoTitle, actualPage.items().get(0).title());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,4,21.1 Implementação dos testes integrados do findAll;Aula de empreendedorismo",
            "1,2,2,4,Não cometa esse erro;System Design",
            "0,4,4,4,21.1 Implementação dos testes integrados do findAll;" +
                    "Aula de empreendedorismo;" +
                    "Não cometa esse erro;" +
                    "System Design"
    })
    public void givenAValidPaging_whenCallsFindAll_shouldReturnPaged(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedVideos
    ) {
        // given
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        mockVideos();

        final var aQuery =
                new SearchQuery(
                        expectedPage,
                        expectedPerPage,
                        expectedTerms,
                        expectedSort,
                        expectedDirection
                );

        // when
        final var actualPage = videoGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());

        int index = 0;
        for (final var expectedTitle : expectedVideos.split(";")) {
            final var actualTitle = actualPage.items().get(index).title();
            Assertions.assertEquals(expectedTitle, actualTitle);
            index++;
        }
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


    private void mockVideos() {
        videoGateway.create(
                Video.newVideo(
                        "System Design",
                        Fixture.Videos.description(),
                        Fixture.duration(),
                        clientId
                )
        );

        videoGateway.create(
                Video.newVideo(
                        "Não cometa esse erro",
                        Fixture.Videos.description(),
                        Fixture.duration(),
                        clientId
                )
        );

        videoGateway.create(
                Video.newVideo(
                        "21.1 Implementação dos testes integrados do findAll",
                        Fixture.Videos.description(),
                        Fixture.duration(),
                        clientId
                )
        );

        videoGateway.create(
                Video.newVideo(
                        "Aula de empreendedorismo",
                        Fixture.Videos.description(),
                        Fixture.duration(),
                        clientId

                )
        );
    }
}