package com.wenox.anonymization.database_restoration_service;

public class ExistsDatabaseException extends RuntimeException {

    public ExistsDatabaseException() {
        super();
    }

    public ExistsDatabaseException(String message) {
        super(message);
    }

    public ExistsDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExistsDatabaseException(Throwable cause) {
        super(cause);
    }
}
