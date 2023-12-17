package com.wenox.anonymization.metadata_extraction_service.domain.exception;

public class MetadataNotFoundException extends RuntimeException {

    public MetadataNotFoundException(String message) {
        super(message);
    }
}
