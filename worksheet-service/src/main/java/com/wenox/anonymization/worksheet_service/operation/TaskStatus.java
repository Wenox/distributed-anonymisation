package com.wenox.anonymization.worksheet_service.operation;

public enum TaskStatus {
    INITIALISED,
    EXTRACTED_COLUMN_TUPLE,
    APPLIED_ANONYMISATION,
    CREATED_FRAGMENT,
    LOADED_FRAGMENT
}
