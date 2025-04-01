package com.fiap.tech.challenge.domain.validation;

import java.util.List;

public interface ValidationHandler {

    void append(Error anError);

    void append(ValidationHandler aHandler);

    List<Error> getErrors();

    default boolean hasErrors() {
        return getErrors() != null && !getErrors().isEmpty();
    }

    interface Validation<T>{
        T validate();
    }
}
