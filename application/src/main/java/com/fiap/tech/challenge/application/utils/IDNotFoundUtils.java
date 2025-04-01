package com.fiap.tech.challenge.application.utils;

import com.fiap.tech.challenge.domain.AggregateRoot;
import com.fiap.tech.challenge.domain.Identifier;
import com.fiap.tech.challenge.domain.exceptions.DomainException;
import com.fiap.tech.challenge.domain.exceptions.NotFoundException;

import java.util.function.Supplier;

public final class IDNotFoundUtils {
    public static Supplier<DomainException> notFound(final Identifier anId, Class<? extends AggregateRoot<?>> anAggregate) {
        return () -> NotFoundException.with(anAggregate, anId);
    }
}
