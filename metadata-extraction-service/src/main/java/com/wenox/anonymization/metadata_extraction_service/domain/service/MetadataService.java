package com.wenox.anonymization.metadata_extraction_service.domain.service;

import com.wenox.anonymization.metadata_extraction_service.domain.model.Metadata;

public interface MetadataService {

    Metadata getMetadataByBlueprintId(String blueprintId);
}