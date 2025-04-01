package com.fiap.tech.challenge.domain.utils;

import java.util.UUID;

public final class IDUtils {

    private IDUtils() {
    }

    public static String uuid() {
        return UUID.randomUUID().toString().toLowerCase().replace("-", "");
    }
}
