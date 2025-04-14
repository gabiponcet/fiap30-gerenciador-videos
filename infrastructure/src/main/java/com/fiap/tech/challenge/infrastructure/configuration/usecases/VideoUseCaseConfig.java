package com.fiap.tech.challenge.infrastructure.configuration.usecases;


import com.fiap.tech.challenge.application.video.create.CreateVideoUseCase;
import com.fiap.tech.challenge.application.video.create.DefaultCreateVideoUseCase;
import com.fiap.tech.challenge.application.video.delete.DefaultDeleteVideoUseCase;
import com.fiap.tech.challenge.application.video.delete.DeleteVideoUseCase;
import com.fiap.tech.challenge.application.video.media.get.DefaultGetMediaUseCase;
import com.fiap.tech.challenge.application.video.media.get.GetMediaUseCase;
import com.fiap.tech.challenge.application.video.media.update.DefaultUpdateMediaStatusUseCase;
import com.fiap.tech.challenge.application.video.media.update.UpdateMediaStatusUseCase;
import com.fiap.tech.challenge.application.video.media.upload.DefaultUploadMediaUseCase;
import com.fiap.tech.challenge.application.video.media.upload.UploadMediaUseCase;
import com.fiap.tech.challenge.application.video.retrieve.get.DefaultGetVideoByIdUseCase;
import com.fiap.tech.challenge.application.video.retrieve.get.GetVideoByIdUseCase;
import com.fiap.tech.challenge.application.video.retrieve.list.DefaultListVideoUseCase;
import com.fiap.tech.challenge.application.video.retrieve.list.ListVideoUseCase;
import com.fiap.tech.challenge.domain.video.AuthenticatedUser;
import com.fiap.tech.challenge.domain.video.MediaResourceGateway;
import com.fiap.tech.challenge.domain.video.VideoGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class VideoUseCaseConfig {

    private final VideoGateway videoGateway;

    private final MediaResourceGateway mediaResourceGateway;

    private final AuthenticatedUser authenticatedUser;

    public VideoUseCaseConfig(
            final VideoGateway videoGateway,
            final MediaResourceGateway mediaResourceGateway,
            final AuthenticatedUser authenticatedUser
    ) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.authenticatedUser = authenticatedUser;
    }

    @Bean
    public CreateVideoUseCase createVideoUseCase() {
        return new DefaultCreateVideoUseCase(
                videoGateway,
                mediaResourceGateway,
                authenticatedUser
        );
    }

    @Bean
    public DeleteVideoUseCase deleteVideoUseCase() {
        return new DefaultDeleteVideoUseCase(videoGateway, mediaResourceGateway, authenticatedUser);
    }

    @Bean
    public GetVideoByIdUseCase getVideoByIdUseCase() {
        return new DefaultGetVideoByIdUseCase(videoGateway);
    }

    @Bean
    public ListVideoUseCase listVideoUseCase() {
        return new DefaultListVideoUseCase(videoGateway);
    }

    @Bean
    public GetMediaUseCase getMediaUseCase() {
        return new DefaultGetMediaUseCase(mediaResourceGateway, authenticatedUser);
    }

    @Bean
    public UploadMediaUseCase uploadMediaUseCase() {
        return new DefaultUploadMediaUseCase(videoGateway, mediaResourceGateway, authenticatedUser);
    }

    @Bean
    public UpdateMediaStatusUseCase updateMediaStatusUseCase() {
        return new DefaultUpdateMediaStatusUseCase(videoGateway);
    }
}
