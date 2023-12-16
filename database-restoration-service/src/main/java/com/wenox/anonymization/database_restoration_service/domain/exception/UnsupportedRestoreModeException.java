package com.wenox.anonymization.database_restoration_service.domain.exception;

public class UnsupportedRestoreModeException extends RuntimeException {

    public UnsupportedRestoreModeException() {
        super();
    }

    public UnsupportedRestoreModeException(String message) {
        super(message);
    }

    public UnsupportedRestoreModeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedRestoreModeException(Throwable cause) {
        super(cause);
    }
}
