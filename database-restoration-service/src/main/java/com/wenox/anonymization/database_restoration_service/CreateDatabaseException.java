package com.wenox.anonymization.database_restoration_service;

public class CreateDatabaseException extends RuntimeException {

    public CreateDatabaseException() {
        super();
    }

    public CreateDatabaseException(String message) {
        super(message);
    }

    public CreateDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateDatabaseException(Throwable cause) {
        super(cause);
    }
}
