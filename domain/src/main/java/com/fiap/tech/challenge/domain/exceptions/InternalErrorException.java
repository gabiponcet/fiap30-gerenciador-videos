package com.fiap.tech.challenge.domain.exceptions;

public class InternalErrorException extends NoStacktraceRuntimeException {

    protected InternalErrorException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public static InternalErrorException with(final String message, final Throwable cause) {
        return new InternalErrorException(message, cause);
    }
}
