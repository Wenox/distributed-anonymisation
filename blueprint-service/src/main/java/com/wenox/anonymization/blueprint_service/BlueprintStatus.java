package com.wenox.anonymization.blueprint_service;

public enum BlueprintStatus {
    CREATED,
    // todo dump store?
    RESTORE_SUCCESS,
    RESTORE_FAILURE,
    METADATA_EXTRACTION_SUCCESS,
    METADATA_EXTRACTION_FAILURE
}
