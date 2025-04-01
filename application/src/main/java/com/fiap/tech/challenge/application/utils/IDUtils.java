package com.fiap.tech.challenge.application.utils;

import com.fiap.tech.challenge.domain.Identifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class IDUtils {

    private IDUtils(){
    }
    public static List<String> asString(final Collection<? extends Identifier> ids) {
        return ids.stream().map(Identifier::getValue).toList();
    }

    public static <T> List<T> asList(final Collection<T> ids) {
        return ids.stream().toList();
    }

    public static <T> Set<T> asSet(final Collection<T> ids) {
        return new HashSet<>(ids);
    }

}
