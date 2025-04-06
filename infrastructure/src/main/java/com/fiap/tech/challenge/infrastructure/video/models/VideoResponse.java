package com.fiap.tech.challenge.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Set;

public record VideoResponse(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("duration") double duration,
        @JsonProperty("video") AudioVideoMediaResponse video
) {
}
