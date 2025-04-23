package com.fiap.tech.challenge.infrastructure.video;

import com.fiap.tech.challenge.domain.Identifier;
import com.fiap.tech.challenge.domain.pagination.Pagination;
import com.fiap.tech.challenge.domain.pagination.SearchQuery;
import com.fiap.tech.challenge.domain.video.*;
import com.fiap.tech.challenge.infrastructure.configuration.annotations.VideoCreatedQueue;
import com.fiap.tech.challenge.infrastructure.services.EventService;
import com.fiap.tech.challenge.infrastructure.utils.SqlUtils;
import com.fiap.tech.challenge.infrastructure.video.persistence.VideoJpaEntity;
import com.fiap.tech.challenge.infrastructure.video.persistence.VideoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DefaultVideoGateway implements VideoGateway {

    private final EventService eventService;
    private final VideoRepository repository;
    private final AuthenticatedUser authenticatedUser;


    public DefaultVideoGateway(
            @VideoCreatedQueue final EventService eventService,
            final VideoRepository repository, AuthenticatedUser authenticatedUser
    ) {
        this.eventService = Objects.requireNonNull(eventService);
        this.repository = Objects.requireNonNull(repository);
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    @Transactional
    public Video create(final Video aVideo) {
        return save(aVideo);
    }

    private Video save(final Video aVideo) {
        final var result = repository.save(VideoJpaEntity.from(aVideo)).toAggregate();

        aVideo.publishDomainEvents(eventService::send);

        return result;
    }

    @Override
    @Transactional
    public Video update(final Video aVideo) {
        return repository.save(VideoJpaEntity.from(aVideo)).toAggregate();
    }

    @Override
    public void deleteById(final VideoID anId) {
        final var videoId = anId.getValue();
        if(this.repository.existsById(videoId)) {
            this.repository.deleteById(videoId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Video> findById(final VideoID anId) {
        final var video = repository.findById(anId.getValue()).map(VideoJpaEntity::toAggregate);
        if(authenticatedUser.getClientId() != null && !authenticatedUser.getClientId().equals(video.get().getClientId())) {
            throw new AccessDeniedException("You are not authorized to access this video");
        }
        return video;
    }

    @Override
    public Pagination<VideoPreview> findAll(SearchQuery aQuery) {
        final var page = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        final var actualPage = this.repository.findAll(
                SqlUtils.like(SqlUtils.upper(aQuery.terms())),
                authenticatedUser.getClientId().getValue(),
                page
        );

        return new Pagination<>(
                actualPage.getNumber(),
                actualPage.getSize(),
                actualPage.getTotalElements(),
                actualPage.toList()
        );
    }

}
