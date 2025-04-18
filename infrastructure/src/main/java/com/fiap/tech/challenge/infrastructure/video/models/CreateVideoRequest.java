package com.fiap.tech.challenge.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record CreateVideoRequest(
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("duration") Double duration

) {

    public static CreateVideoRequest with(final String title,
                                          final String description,
                                          final Double duration) {
        return new CreateVideoRequest(
                title,
                description,
                duration
        );
    }
}
