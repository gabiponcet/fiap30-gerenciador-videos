package com.fiap.tech.challenge.application.video.retrieve.list;

import com.fiap.tech.challenge.domain.pagination.Pagination;
import com.fiap.tech.challenge.domain.pagination.SearchQuery;
import com.fiap.tech.challenge.domain.video.VideoGateway;

import java.util.Objects;

public class DefaultListVideoUseCase extends ListVideoUseCase {

    private final VideoGateway videoGateway;

    public DefaultListVideoUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public Pagination<VideoListOutput> execute(final SearchQuery aQuery) {
        return videoGateway.findAll(aQuery).map(VideoListOutput::from);
    }
}
