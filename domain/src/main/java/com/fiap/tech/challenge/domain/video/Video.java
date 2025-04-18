package com.fiap.tech.challenge.domain.video;

import com.fiap.tech.challenge.domain.AggregateRoot;
import com.fiap.tech.challenge.domain.events.DomainEvent;
import com.fiap.tech.challenge.domain.utils.InstantUtils;
import com.fiap.tech.challenge.domain.validation.ValidationHandler;

import java.time.Instant;
import java.time.Year;
import java.util.*;

public class Video extends AggregateRoot<VideoID> {
    private String title;
    private String description;
    private double duration;
    private ClientID clientId;
    private AudioVideoMedia video;

    protected Video(
            final VideoID anId,
            final String aTitle,
            final String aDescription,
            final double aDuration,
            final ClientID aClientId,
            final AudioVideoMedia aVideo,

            final List<DomainEvent> domainEvents
    ) {
        super(anId, domainEvents);
        this.title = aTitle;
        this.description = aDescription;
        this.duration = aDuration;
        this.clientId = aClientId;
        this.video = aVideo;
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new VideoValidator(this, handler).validate();
    }

    public static Video newVideo(
            final String aTitle,
            final String aDescription,
            final double aDuration,
            final ClientID clientId
    ) {
        final var anId = VideoID.unique();
        return new Video(
                anId,
                aTitle,
                aDescription,
                aDuration,
                clientId,
                null,
                null
        );
    }

    public Video update(
            final String aTitle,
            final String aDescription,
            final double aDuration
    ) {
        this.title = aTitle;
        this.description = aDescription;
        this.duration = aDuration;
        return this;
    }

    public static Video with(
            final VideoID anId,
            final String aTitle,
            final String aDescription,
            final double aDuration,
            final ClientID aClientId,
            final AudioVideoMedia aVideo
    ) {
        return new Video(
                anId,
                aTitle,
                aDescription,
                aDuration,
                aClientId,
                aVideo,
                null
        );
    }

    public static Video with(
            final Video aVideo
    ) {
        return new Video(
                aVideo.getId(),
                aVideo.getTitle(),
                aVideo.getDescription(),
                aVideo.getDuration(),
                aVideo.getClientId(),
                aVideo.getVideo().orElse(null),
                aVideo.getDomainEvents()
        );
    }

    public ClientID getClientId() {
        return clientId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getDuration() {
        return duration;
    }

    public Optional<AudioVideoMedia> getVideo() {
        return Optional.ofNullable(video);
    }


    private void onAudioVideoMediaUpdate(final AudioVideoMedia media) {
        if (media != null && media.isPendingEncode()) {
            this.registerEvent(new VideoMediaCreated(getId().getValue(), getClientId().getValue(), media.id(), media.rawLocation()));
        }
    }

    public Video updateVideoMedia(final AudioVideoMedia video) {
        this.video = video;
        onAudioVideoMediaUpdate(video);
        return this;
    }

    public void processing(final VideoMediaType aType) {
        if(VideoMediaType.VIDEO == aType) {
            getVideo().ifPresent(media -> updateVideoMedia(media.processing()));
        }
    }

    public void error(final VideoMediaType aType) {
        if(VideoMediaType.VIDEO == aType) {
            getVideo().ifPresent(media -> updateVideoMedia(media.error()));
        }
    }

    public void completed(final VideoMediaType aType, final String encodedPath) {
        if(VideoMediaType.VIDEO == aType) {
            getVideo().ifPresent(media -> updateVideoMedia(media.completed(encodedPath)));
        }
    }
}
