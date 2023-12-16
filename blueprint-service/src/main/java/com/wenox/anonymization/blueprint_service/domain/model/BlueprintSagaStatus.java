package com.wenox.anonymization.blueprint_service.domain.model;

public enum BlueprintSagaStatus {
    CREATED,
    DUMP_STORE_SUCCESS,
    DUMP_STORE_FAILURE,
    RESTORE_SUCCESS,
    RESTORE_FAILURE,
    METADATA_EXTRACTION_SUCCESS,
    METADATA_EXTRACTION_FAILURE
}
