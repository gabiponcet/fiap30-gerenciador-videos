package com.fiap.tech.challenge.domain.validation;

public abstract class Validator {

    private final ValidationHandler handler;

    protected Validator(final ValidationHandler aHandler) {
        this.handler = aHandler;
    }

    public abstract void validate();

    public ValidationHandler getHandler() {
        return handler;
    }
}
