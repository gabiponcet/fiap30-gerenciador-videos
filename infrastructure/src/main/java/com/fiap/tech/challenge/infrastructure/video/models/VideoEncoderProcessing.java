package com.fiap.tech.challenge.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("PROCESSING")
public record VideoEncoderProcessing(
        @JsonProperty("id") String id,
        @JsonProperty("video") VideoMetadata video

) implements VideoEncoderResult {

    private static final String COMPLETED = "PROCESSING";

    @Override
    public String getStatus() {
        return COMPLETED;
    }
}
