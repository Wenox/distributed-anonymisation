package com.wenox.anonymization.database_restoration_service.domain.exception;

public class DropDatabaseException extends RuntimeException {

    public DropDatabaseException() {
        super();
    }

    public DropDatabaseException(String message) {
        super(message);
    }

    public DropDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DropDatabaseException(Throwable cause) {
        super(cause);
    }
}
