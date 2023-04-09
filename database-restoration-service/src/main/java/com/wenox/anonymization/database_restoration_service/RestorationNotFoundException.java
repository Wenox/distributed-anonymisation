package com.wenox.anonymization.database_restoration_service;

public class RestorationNotFoundException extends RuntimeException {

    public RestorationNotFoundException(String message) {
        super(message);
    }
}
