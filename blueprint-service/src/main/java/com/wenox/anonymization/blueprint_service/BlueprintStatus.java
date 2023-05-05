package com.wenox.anonymization.blueprint_service;

public enum BlueprintStatus {
    CREATED,
    S3_STORE_SUCCESS,
    S3_STORE_FAILURE,
    RESTORE_SUCCESS,
    RESTORE_FAILURE,
    METADATA_EXTRACTION_SUCCESS,
    METADATA_EXTRACTION_FAILURE
}
