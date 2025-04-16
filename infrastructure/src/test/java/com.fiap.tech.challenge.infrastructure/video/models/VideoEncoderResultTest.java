package com.fiap.tech.challenge.infrastructure.video.models;


import com.fiap.tech.challenge.domain.utils.IDUtils;
import com.fiap.tech.challenge.infrastructure.JacksonTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonTest
public class VideoEncoderResultTest {

    @Autowired
    private JacksonTester<VideoEncoderResult> json;

    @Test
    void testUnmarshallSuccessResult() throws IOException {
        final var expectedId = IDUtils.uuid();
        final var expectedStatus = "COMPLETED";
        final var expectedEncoderVideoFolder = "anyfolder";
        final var expectedResourceId = IDUtils.uuid();
        final var expectedFilePath = "any.mp4";
        final var expectedMetadata = new VideoMetadata(expectedEncoderVideoFolder, expectedResourceId, expectedFilePath);

        final var json = """
                {
                    "id": "%s",
                    "status": "%s",
                    "video": {
                        "encoded_video_folder": "%s",
                        "resource_id": "%s",
                        "file_path": "%s"
                    }
                }
                """.formatted(
                expectedId,
                expectedStatus,
                expectedEncoderVideoFolder,
                expectedResourceId,
                expectedFilePath
        );

        final var actualResult = this.json.parse(json);

        assertThat(actualResult)
                .isInstanceOf(VideoEncoderCompleted.class)
                .hasFieldOrPropertyWithValue("id", expectedId)
                .hasFieldOrPropertyWithValue("status", expectedStatus)
                .hasFieldOrPropertyWithValue("video", expectedMetadata);
    }

    @Test
    void testMarshallSuccessResult() throws IOException {
        final var expectedId = IDUtils.uuid();
        final var expectedStatus = "COMPLETED";
        final var expectedEncoderVideoFolder = "anyfolder";
        final var expectedResourceId = IDUtils.uuid();
        final var expectedFilePath = "any.mp4";
        final var expectedMetadata = new VideoMetadata(expectedEncoderVideoFolder, expectedResourceId, expectedFilePath);
        final var expectedSuccessMessage = new VideoEncoderCompleted(expectedId, expectedMetadata);

        final var actualResult = this.json.write(expectedSuccessMessage);

        assertThat(actualResult)
                .hasJsonPathValue("$.id", expectedId)
                .hasJsonPathValue("$.status", expectedStatus)
                .hasJsonPathValue("$.video.encoded_video_folder", expectedEncoderVideoFolder)
                .hasJsonPathValue("$.video.resource_id", expectedResourceId)
                .hasJsonPathValue("$.video.file_path", expectedFilePath)
        ;
    }

    @Test
    void testUnmarshallErrorResult() throws IOException {
        final var expectedMessage = "Resource not found";
        final var expectedStatus = "ERROR";
        final var expectedResourceId = IDUtils.uuid();
        final var expectedFilePath = "any.mp4";
        final var expectedVideoMessage = new VideoMessage(expectedResourceId, expectedFilePath);

        final var json = """
                {
                    "status": "%s",
                    "error": "%s",
                    "message": {
                        "resource_id": "%s",
                        "file_path": "%s"
                    }
                }
                """.formatted(

                expectedStatus,
                expectedMessage,
                expectedResourceId,
                expectedFilePath
        );

        final var actualResult = this.json.parse(json);

        assertThat(actualResult)
                .isInstanceOf(VideoEncoderError.class)
                .hasFieldOrPropertyWithValue("error", expectedMessage)
                .hasFieldOrPropertyWithValue("status", expectedStatus)
                .hasFieldOrPropertyWithValue("message", expectedVideoMessage);
    }

}
