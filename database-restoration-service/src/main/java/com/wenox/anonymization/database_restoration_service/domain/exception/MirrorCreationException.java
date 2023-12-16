package com.wenox.anonymization.database_restoration_service.domain.exception;

public class MirrorCreationException extends RuntimeException {

    public MirrorCreationException() {
        super();
    }

    public MirrorCreationException(String message) {
        super(message);
    }

    public MirrorCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MirrorCreationException(Throwable cause) {
        super(cause);
    }
}