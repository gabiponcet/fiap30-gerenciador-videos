package com.fiap.tech.challenge.infrastructure.video.persistence;

import com.fiap.tech.challenge.domain.video.Video;
import com.fiap.tech.challenge.domain.video.VideoID;

import javax.persistence.*;
import java.util.Optional;

public class VideoJpaEntity {

    @Id
    private String id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "duration", precision = 2)
    private double duration;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "video_id")
    private AudioVideoMediaJpaEntity video;

    public VideoJpaEntity() {
    }

    private VideoJpaEntity(
            final String id,
            final String title,
            final String description,
            final double duration,
            final AudioVideoMediaJpaEntity video
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;

        this.video = video;

    }

    public static VideoJpaEntity from(final Video aVideo) {
        final var entity = new VideoJpaEntity(
                aVideo.getId().getValue(),
                aVideo.getTitle(),
                aVideo.getDescription(),
                aVideo.getDuration(),
                aVideo.getVideo()
                        .map(AudioVideoMediaJpaEntity::from)
                        .orElse(null)
        );

        return entity;
    }

    public Video toAggregate() {
        return Video.with(
                VideoID.from(getId()),
                getTitle(),
                getDescription(),
                getDuration(),
                Optional.ofNullable(getVideo())
                        .map(AudioVideoMediaJpaEntity::toDomain)
                        .orElse(null)
        );
    }

    public String getId() {
        return id;
    }

    public VideoJpaEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public VideoJpaEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public VideoJpaEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public double getDuration() {
        return duration;
    }

    public VideoJpaEntity setDuration(double duration) {
        this.duration = duration;
        return this;
    }

    public AudioVideoMediaJpaEntity getVideo() {
        return video;
    }

    public VideoJpaEntity setVideo(AudioVideoMediaJpaEntity video) {
        this.video = video;
        return this;
    }
}
