package com.wenox.anonymization.database_restoration_service.domain.exception;

// todo: exception handler
public class RestorationNotFoundException extends RuntimeException {

    public RestorationNotFoundException(String message) {
        super(message);
    }
}
