package com.fiap.tech.challenge.domain.video;

import java.util.Arrays;
import java.util.Optional;

public enum VideoMediaType {
    VIDEO, ZIP;

    public static Optional<VideoMediaType> of(final String value) {
        return Arrays.stream(values()).filter(type -> type.name().equalsIgnoreCase(value)).
                findFirst();
    }
}
