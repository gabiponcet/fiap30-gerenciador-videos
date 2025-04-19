package com.fiap.tech.challenge.application.video.retrieve.list;

import com.fiap.tech.challenge.application.UseCaseTest;
import com.fiap.tech.challenge.domain.Fixture;
import com.fiap.tech.challenge.domain.pagination.Pagination;
import com.fiap.tech.challenge.domain.pagination.SearchQuery;
import com.fiap.tech.challenge.domain.video.VideoGateway;
import com.fiap.tech.challenge.domain.video.VideoPreview;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DefaultListVideoUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultListVideoUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway);
    }

    @Test
    public void givenAValidQuery_whenCallsListVideos_shouldReturnVideos() throws Exception {
        final var videos = List.of(
                VideoPreview.from(Fixture.video()),
                VideoPreview.from(Fixture.video())
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;
        final var expectedItems = videos.stream().map(VideoListOutput::from).toList();

        final var expectedPagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                videos
        );

        when(videoGateway.findAll(any())).thenReturn(expectedPagination);

        final var aQuery =
                new SearchQuery(
                        expectedPage,
                        expectedPerPage,
                        expectedTerms,
                        expectedSort,
                        expectedDirection
                );

        final var actualOutput = useCase.execute(aQuery);

        assertEquals(expectedPage, actualOutput.currentPage());
        assertEquals(expectedPerPage, actualOutput.perPage());
        assertEquals(expectedTotal, actualOutput.total());
        assertEquals(expectedItems, actualOutput.items());

        verify(videoGateway, times(1)).findAll(eq(aQuery));
    }

    @Test
    public void givenAnValidQuery_whenCallsListVideosAndResultIsEmpty_shouldReturnGenres() {
        final var videos = List.<VideoPreview>of(
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var expectedItems =  List.<VideoListOutput>of(
        );

        final var expectedPagination = new Pagination<>(
                expectedPage,
                expectedPerPage,
                expectedTotal,
                videos
        );

        when(videoGateway.findAll(any())).thenReturn(expectedPagination);

        final var aQuery =
                new SearchQuery(
                        expectedPage,
                        expectedPerPage,
                        expectedTerms,
                        expectedSort,
                        expectedDirection
                );

        final var actualOutput = useCase.execute(aQuery);

        assertEquals(expectedPage, actualOutput.currentPage());
        assertEquals(expectedPerPage, actualOutput.perPage());
        assertEquals(expectedTotal, actualOutput.currentPage());
        assertEquals(expectedItems, actualOutput.items());

        verify(videoGateway, times(1)).findAll(eq(aQuery));
    }

    @Test
    public void givenAnValidQuery_whenCallsListVideoAndGatewayThrowsRandom_shouldThrowsException() {

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "A";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedErrorMessage = "Gateway error";

        doThrow(new IllegalStateException(expectedErrorMessage)).when(videoGateway).findAll(any());

        final var aQuery =
                new SearchQuery(
                        expectedPage,
                        expectedPerPage,
                        expectedTerms,
                        expectedSort,
                        expectedDirection
                );

        final var actualException = assertThrows(IllegalStateException.class, () -> useCase.execute(aQuery));

        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(videoGateway, times(1)).findAll(eq(aQuery));
    }
}
