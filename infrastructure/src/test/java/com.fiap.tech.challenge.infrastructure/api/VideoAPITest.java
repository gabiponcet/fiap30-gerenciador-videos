package com.fiap.tech.challenge.infrastructure.api;

import com.fiap.tech.challenge.application.video.create.CreateVideoCommand;
import com.fiap.tech.challenge.application.video.create.CreateVideoOutput;
import com.fiap.tech.challenge.application.video.create.CreateVideoUseCase;
import com.fiap.tech.challenge.application.video.delete.DeleteVideoUseCase;
import com.fiap.tech.challenge.application.video.media.get.GetMediaCommand;
import com.fiap.tech.challenge.application.video.media.get.GetMediaUseCase;
import com.fiap.tech.challenge.application.video.media.get.MediaOutput;
import com.fiap.tech.challenge.application.video.media.upload.UploadMediaCommand;
import com.fiap.tech.challenge.application.video.media.upload.UploadMediaOutput;
import com.fiap.tech.challenge.application.video.media.upload.UploadMediaUseCase;
import com.fiap.tech.challenge.application.video.retrieve.get.GetVideoByIdUseCase;
import com.fiap.tech.challenge.application.video.retrieve.get.VideoOutput;
import com.fiap.tech.challenge.application.video.retrieve.list.ListVideoUseCase;
import com.fiap.tech.challenge.application.video.retrieve.list.VideoListOutput;
import com.fiap.tech.challenge.domain.Fixture;
import com.fiap.tech.challenge.domain.exceptions.NotFoundException;
import com.fiap.tech.challenge.domain.exceptions.NotificationException;
import com.fiap.tech.challenge.domain.pagination.Pagination;
import com.fiap.tech.challenge.domain.pagination.SearchQuery;
import com.fiap.tech.challenge.domain.validation.Error;
import com.fiap.tech.challenge.domain.video.*;
import com.fiap.tech.challenge.infrastructure.ApiTest;
import com.fiap.tech.challenge.infrastructure.ControllerTest;
import com.fiap.tech.challenge.infrastructure.video.models.CreateVideoRequest;
import com.fiap.tech.challenge.infrastructure.video.models.UpdateVideoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.fiap.tech.challenge.domain.utils.CollectionUtils.mapTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = VideoAPI.class)
class VideoAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateVideoUseCase createVideoUseCase;

    @MockBean
    private GetVideoByIdUseCase getVideoByIdUseCase;


    @MockBean
    private DeleteVideoUseCase deleteVideoUseCase;

    @MockBean
    private ListVideoUseCase listVideoUseCase;

    @MockBean
    private GetMediaUseCase getMediaUseCase;

    @MockBean
    private UploadMediaUseCase uploadMediaUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateFull_shouldReturnAnId() throws Exception {


        final var expectedId = VideoID.unique();
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.video().getDescription();
        final var expectedDuration = Fixture.duration();

        final var expectedVideo =
                new MockMultipartFile("video_file", "video.mp4", "video/mp4", "VIDEO".getBytes());

        when(createVideoUseCase.execute(any())).thenReturn(new CreateVideoOutput(expectedId.getValue()));

        final var aRequest = multipart("/videos")
                .file(expectedVideo)
                .param("title", expectedTitle)
                .param("description", expectedDescription)
                .param("duration", expectedDuration.toString())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(ApiTest.USER_JWT)
                ;

        this.mvc.perform(aRequest)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/videos/" + expectedId.getValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())))
        ;

        final var cmdCaptor = ArgumentCaptor.forClass(CreateVideoCommand.class);

        verify(createVideoUseCase).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        assertEquals(expectedTitle, actualCmd.title());
        assertEquals(expectedDescription, actualCmd.description());
        assertEquals(expectedDuration, actualCmd.duration());
        assertEquals(expectedVideo.getOriginalFilename(), actualCmd.getVideo().get().name());
    }

    @Test
    public void givenAnInvalidCommand_whenCallsCreateFull_shouldReturnError() throws Exception {
        final var expectedErrorMessage = "title is required";
        when(createVideoUseCase.execute(any())).thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        final var aRequest = multipart("/videos")
                .with(ApiTest.USER_JWT)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        this.mvc.perform(aRequest)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
        ;

    }

    @Test
    public void givenAValidCommand_whenCallsCreatePartial_shouldReturnAnId() throws Exception {
        final var expectedId = VideoID.unique();
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedDuration = Fixture.duration();


        final var aCommand = CreateVideoRequest.with(
                expectedTitle,
                expectedDescription,
                expectedDuration
        );

        when(createVideoUseCase.execute(any())).thenReturn(new CreateVideoOutput(expectedId.getValue()));

        final var aRequest = post("/videos")
                .with(ApiTest.USER_JWT)
                .content(objectMapper.writeValueAsBytes(aCommand))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(aRequest)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/videos/" + expectedId.getValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())))
        ;

        final var cmdCaptor = ArgumentCaptor.forClass(CreateVideoCommand.class);

        verify(createVideoUseCase).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        assertEquals(expectedTitle, actualCmd.title());
        assertEquals(expectedDescription, actualCmd.description());
        assertEquals(expectedDuration, actualCmd.duration());
        assertTrue(actualCmd.getVideo().isEmpty());
    }

    @Test
    public void givenAnInvalidCommand_whenCallsCreatePartial_shouldReturnError() throws Exception {
        final var expectedErrorMessage = "title is required";


        when(createVideoUseCase.execute(any())).thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        final var aRequest = post("/videos")
                .with(ApiTest.USER_JWT)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                                {
                                  "title":"title"
                                }
                                """
                );

        this.mvc.perform(aRequest)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
        ;

    }

    @Test
    public void givenAnEmptyCommand_whenCallsCreatePartial_shouldReturnError() throws Exception {
        final var aRequest = post("/videos")
                .with(ApiTest.USER_JWT)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);


        this.mvc.perform(aRequest)
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    public void givenAValidId_whenCallsGetById_shouldReturnVideo() throws Exception {
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedDuration = Fixture.duration();
        final var expectedClientId = Fixture.Videos.clientId();


        final var expectedVideo = Fixture.Videos.audioVideo(VideoMediaType.VIDEO);

        final var aVideo = Video.newVideo(
                        expectedTitle,
                        expectedDescription,
                        expectedDuration,
                        expectedClientId
                ).updateVideoMedia(expectedVideo);

        final var expectedId = aVideo.getId().getValue();

        when(getVideoByIdUseCase.execute(any())).thenReturn(VideoOutput.from(aVideo));

        final var aRequest = get("/videos/{id}", expectedId)
                .with(ApiTest.USER_JWT)
                .accept(MediaType.APPLICATION_JSON);

        final var aResponse = this.mvc.perform(aRequest).andDo(print());

        aResponse.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.title", equalTo(expectedTitle)))
                .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
                .andExpect(jsonPath("$.duration", equalTo(expectedDuration)))
                .andExpect(jsonPath("$.video.id", equalTo(expectedVideo.id())))
                .andExpect(jsonPath("$.video.name", equalTo(expectedVideo.name())))
                .andExpect(jsonPath("$.video.checksum", equalTo(expectedVideo.checksum())))
                .andExpect(jsonPath("$.video.location", equalTo(expectedVideo.rawLocation())))
                .andExpect(jsonPath("$.video.encoded_location", equalTo(expectedVideo.encodedLocation())))
                .andExpect(jsonPath("$.video.status", equalTo(expectedVideo.status().name())));
    }

    @Test
    public void givenAnInvalidId_whenCallsGetById_shouldReturnVideo() throws Exception {
        final var expectedId = VideoID.unique();

        final var expectedErrorMessage = "Video with ID %s was not found".formatted(expectedId.getValue());

        when(getVideoByIdUseCase.execute(any())).thenThrow(NotFoundException.with(Video.class, expectedId));

        final var aRequest = get("/videos/{id}", expectedId)
                .with(ApiTest.USER_JWT)
                .accept(MediaType.APPLICATION_JSON);

        final var aResponse = this.mvc.perform(aRequest).andDo(print());

        aResponse.andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

    }

    @Test
    public void givenAValidId_whenCallsDeleteById_shouldDeleteIt() throws Exception {
        final var expectedId = VideoID.unique();

        doNothing().when(deleteVideoUseCase).execute(any());

        final var aRequest = delete("/videos/{id}", expectedId.getValue())
                .with(ApiTest.USER_JWT);

        final var response = this.mvc.perform(aRequest).andDo(print());

        response.andExpect(status().isNoContent());

        verify(deleteVideoUseCase).execute(eq(expectedId.getValue()));
    }

    @Test
    public void givenValidParams_whenCallsListVideos_shouldReturnPagination() throws Exception {
        final var aVideo = VideoPreview.from(Fixture.video());

        final var expectedPage = 50;
        final var expectedPerPage = 50;
        final var expectedTerms = "Algo";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedCastMembers = "cast1";
        final var expectedGenres = "gen1";
        final var expectedCategories = "cat1";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(VideoListOutput.from(aVideo));

        when(listVideoUseCase.execute(any())).thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        final var aRequest = get("/videos")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms)
                .accept(MediaType.APPLICATION_JSON)
                .with(ApiTest.USER_JWT);

        final var response = this.mvc.perform(aRequest).andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aVideo.id())))
                .andExpect(jsonPath("$.items[0].title", equalTo(aVideo.title())))
                .andExpect(jsonPath("$.items[0].description", equalTo(aVideo.description())));

        final var captor = ArgumentCaptor.forClass(SearchQuery.class);

        verify(listVideoUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();

        assertEquals(expectedPage, actualQuery.page());
        assertEquals(expectedPerPage, actualQuery.perPage());
        assertEquals(expectedDirection, actualQuery.direction());
        assertEquals(expectedSort, actualQuery.sort());
        assertEquals(expectedTerms, actualQuery.terms());
    }


    @Test
    public void givenEmptyParams_whenCallsListVideosWithDefaultValues_shouldReturnPagination() throws Exception {
        final var aVideo = VideoPreview.from(Fixture.video());

        final var expectedPage = 0;
        final var expectedPerPage = 25;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(VideoListOutput.from(aVideo));

        when(listVideoUseCase.execute(any())).thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        final var aRequest = get("/videos")
                .with(ApiTest.USER_JWT)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest).andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aVideo.id())))
                .andExpect(jsonPath("$.items[0].title", equalTo(aVideo.title())))
                .andExpect(jsonPath("$.items[0].description", equalTo(aVideo.description())));

        final var captor = ArgumentCaptor.forClass(SearchQuery.class);

        verify(listVideoUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();

        assertEquals(expectedPage, actualQuery.page());
        assertEquals(expectedPerPage, actualQuery.perPage());
        assertEquals(expectedDirection, actualQuery.direction());
        assertEquals(expectedSort, actualQuery.sort());
        assertEquals(expectedTerms, actualQuery.terms());
    }

    @Test
    public void givenAValidVideoIdAndFileType_whenCallsGetMediaById_ShouldReturnContent() throws Exception {
        final var expectedId = VideoID.unique();

        final var expectedMediaType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedMediaType);

        final var expectedMedia = new MediaOutput(
                expectedResource.content(),
                expectedResource.contentType(),
                expectedResource.name()
        );

        when(getMediaUseCase.execute(any())).thenReturn(expectedMedia);

        final var aRequest = get("/videos/{id}/medias/{type}", expectedId.getValue(), expectedMediaType.name())
                .with(ApiTest.USER_JWT);

        final var response = this.mvc.perform(aRequest).andDo(print());

        response.andExpect(status().isOk())
                .andExpect(header().string(CONTENT_TYPE, expectedMedia.contentType()))
                .andExpect(header().string(CONTENT_LENGTH, String.valueOf(expectedMedia.content().length)))
                .andExpect(header().string(CONTENT_DISPOSITION, "attachment; filename=" + expectedMedia.name()))
                .andExpect(content().bytes(expectedMedia.content()));

        final var captor = ArgumentCaptor.forClass(GetMediaCommand.class);

        verify(this.getMediaUseCase).execute(captor.capture());

        final var actualCmd = captor.getValue();

        assertEquals(expectedId.getValue(), actualCmd.videoId());
        assertEquals(expectedMediaType.name(), actualCmd.mediaType());
    }

    @Test
    public void givenAValidVideoIdAndFile_whenCallsUploadMedia_shouldStoreIt() throws Exception {
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedVideoId = VideoID.unique();
        final var expectedResource = Fixture.Videos.resource(expectedType);

        final var expectedVideo =
                new MockMultipartFile("media_file", expectedResource.name(), expectedResource.contentType(), expectedResource.content());

        when(uploadMediaUseCase.execute(any())).thenReturn(new UploadMediaOutput(expectedVideoId.getValue(), expectedType));

        final var aRequest = multipart("/videos/{id}/medias/{type}", expectedVideoId.getValue(), expectedType.name())
                .file(expectedVideo)
                .with(ApiTest.USER_JWT)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        final var response = this.mvc.perform(aRequest).andDo(print());

        response.andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/videos/%s/medias/%s".formatted(expectedVideoId.getValue(), expectedType.name())))
                .andExpect(header().string(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.video_id", equalTo(expectedVideoId.getValue())))
                .andExpect(jsonPath("$.media_type", equalTo(expectedType.name())));

        final var captor = ArgumentCaptor.forClass(UploadMediaCommand.class);

        verify(this.uploadMediaUseCase).execute(captor.capture());

        final var actualCmd = captor.getValue();

        assertEquals(expectedVideoId.getValue(), actualCmd.videoId());
        assertEquals(expectedResource.content(), actualCmd.videoResource().resource().content());
        assertEquals(expectedResource.contentType(), actualCmd.videoResource().resource().contentType());
        assertEquals(expectedType, actualCmd.videoResource().type());
    }

    @Test
    public void givenAnInvalidMediaType_whenCallsUploadMedia_shouldReturnErrors() throws Exception {
        final var expectedVideoId = VideoID.unique();
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

        final var expectedVideo =
                new MockMultipartFile("media_file", expectedResource.name(), expectedResource.contentType(), expectedResource.content());

        final var aRequest = multipart("/videos/{id}/medias/INVALID", expectedVideoId.getValue())
                .file(expectedVideo)
                .with(ApiTest.USER_JWT)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        final var response = this.mvc.perform(aRequest).andDo(print());

        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo("Invalid INVALID for VideoMediaType")));

    }
}