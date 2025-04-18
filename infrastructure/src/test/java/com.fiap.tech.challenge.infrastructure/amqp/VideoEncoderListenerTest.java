package com.fiap.tech.challenge.infrastructure.amqp;

import com.fiap.tech.challenge.application.video.media.update.UpdateMediaStatusCommand;
import com.fiap.tech.challenge.application.video.media.update.UpdateMediaStatusUseCase;
import com.fiap.tech.challenge.domain.utils.IDUtils;
import com.fiap.tech.challenge.domain.video.MediaStatus;
import com.fiap.tech.challenge.infrastructure.AmqpTest;
import com.fiap.tech.challenge.infrastructure.configuration.Json;
import com.fiap.tech.challenge.infrastructure.configuration.annotations.VideoEncodedQueue;
import com.fiap.tech.challenge.infrastructure.configuration.properties.amqp.QueueProperties;
import com.fiap.tech.challenge.infrastructure.video.models.VideoEncoderCompleted;
import com.fiap.tech.challenge.infrastructure.video.models.VideoEncoderError;
import com.fiap.tech.challenge.infrastructure.video.models.VideoMessage;
import com.fiap.tech.challenge.infrastructure.video.models.VideoMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@AmqpTest
public class VideoEncoderListenerTest {

    @Autowired
    private TestRabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitListenerTestHarness harness;

    @MockBean
    private UpdateMediaStatusUseCase updateMediaStatusUseCase;

    @Autowired
    @VideoEncodedQueue
    private QueueProperties queueProperties;

    @Test
    public void givenErrorResult_whenCallsListener_shouldProcess() throws InterruptedException {
        final var expectedId = IDUtils.uuid();
        final var expectedStatus = MediaStatus.ERROR;
        final var expectedEncoderVideoFolder = "anyfolder";
        final var expectedResourceId = IDUtils.uuid();
        final var expectedFilePath = "any.mp4";
        final var expectedMetadata = new VideoMetadata(expectedEncoderVideoFolder, expectedResourceId, expectedFilePath);
        final var expectedError = new VideoEncoderError(
                expectedId,
                expectedMetadata
        );

        final var expectedMessage = Json.writeValueAsString(expectedError);

        this.rabbitTemplate.convertAndSend(queueProperties.getQueue(), expectedMessage);

        final var invocationData = harness.getNextInvocationDataFor(VideoEncoderListener.LISTENER_ID, 1, TimeUnit.SECONDS);

        assertNotNull(invocationData);
        assertNotNull(invocationData.getArguments());

        final var actualMessage = (String) invocationData.getArguments()[0];

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void GivenCompletedResult_whenCallsListener_shouldProcess() throws InterruptedException {
        final var expectedId = IDUtils.uuid();
        final var expectedStatus = MediaStatus.COMPLETED;
        final var expectedEncoderVideoFolder = "anyfolder";
        final var expectedResourceId = IDUtils.uuid();
        final var expectedFilePath = "any.mp4";
        final var expectedMetadata = new VideoMetadata(expectedEncoderVideoFolder, expectedResourceId, expectedFilePath);

        final var expectedResult = new VideoEncoderCompleted(
                expectedId,
                expectedMetadata
        );

        final var expectedMessage = Json.writeValueAsString(expectedResult);

        doNothing().when(updateMediaStatusUseCase).execute(any());

        this.rabbitTemplate.convertAndSend(queueProperties.getQueue(), expectedMessage);

        final var invocationData = harness.getNextInvocationDataFor(VideoEncoderListener.LISTENER_ID, 1, TimeUnit.SECONDS);

        assertNotNull(invocationData);
        assertNotNull(invocationData.getArguments());

        final var actualMessage = (String) invocationData.getArguments()[0];

        assertEquals(expectedMessage, actualMessage);

        final var cmdCaptor = ArgumentCaptor.forClass(UpdateMediaStatusCommand.class);
        verify(updateMediaStatusUseCase).execute(cmdCaptor.capture());

        final var actualCommand = cmdCaptor.getValue();

        assertEquals(expectedId, actualCommand.videoId());
        assertEquals(expectedStatus, actualCommand.status());
        assertEquals(expectedEncoderVideoFolder, actualCommand.folder());
        assertEquals(expectedResourceId, actualCommand.resourceId());
        assertEquals(expectedFilePath, actualCommand.filename());
    }
}
