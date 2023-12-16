package com.wenox.anonymization.database_restoration_service.domain.exception;

public class InactiveRestorationException extends RuntimeException {

    public InactiveRestorationException() {
        super();
    }

    public InactiveRestorationException(String message) {
        super(message);
    }

    public InactiveRestorationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InactiveRestorationException(Throwable cause) {
        super(cause);
    }
}
