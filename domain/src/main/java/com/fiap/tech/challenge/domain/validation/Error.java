package com.fiap.tech.challenge.domain.validation;

public record Error(
        String message
) {

    public static Error of(final String message) {
        return new Error(message);
    }
}
