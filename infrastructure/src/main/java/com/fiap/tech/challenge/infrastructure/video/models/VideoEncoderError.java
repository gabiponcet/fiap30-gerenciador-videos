package com.fiap.tech.challenge.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ERROR")
public record VideoEncoderError(
        @JsonProperty("id") String id,
        @JsonProperty("video") VideoMetadata video
) implements VideoEncoderResult {

    private static final String ERROR = "ERROR";

    @Override
    public String getStatus() {
        return ERROR;
    }
}
