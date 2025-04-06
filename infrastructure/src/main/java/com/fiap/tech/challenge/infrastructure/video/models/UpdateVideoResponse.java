package com.fiap.tech.challenge.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateVideoResponse(
        @JsonProperty("id") String id
) {

    public static UpdateVideoResponse with(final String id) {
        return new UpdateVideoResponse(id);
    }
}
