package com.wenox.anonymization.database_restoration_service;

public class WorksheetNotFoundException extends RuntimeException {

    public WorksheetNotFoundException() {
        super();
    }

    public WorksheetNotFoundException(String message) {
        super(message);
    }

    public WorksheetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorksheetNotFoundException(Throwable cause) {
        super(cause);
    }
}
