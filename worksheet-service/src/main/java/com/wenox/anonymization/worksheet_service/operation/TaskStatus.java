package com.wenox.anonymization.worksheet_service.operation;

public enum TaskStatus {
    CREATED,
    EXTRACTED,
    TRANSFORMED_ANONYMIZATION,
    TRANSFORMED_SQL_SCRIPT,
    FINISHED,
    FAILED
}
