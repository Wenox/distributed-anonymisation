package com.wenox.anonymization.metadata_extraction_service.domain.service;

import com.wenox.anonymization.metadata_extraction_service.domain.model.Metadata;
import com.wenox.anonymization.metadata_extraction_service.domain.exception.MetadataNotFoundException;
import com.wenox.anonymization.metadata_extraction_service.domain.port.MetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DefaultMetadataService implements MetadataService {

    private final MetadataRepository metadataRepository;

    @Override
    public Metadata getMetadataByBlueprintId(String blueprintId) {
        return metadataRepository.findByBlueprintId(blueprintId)
                .orElseThrow(() -> new MetadataNotFoundException("Metadata not found with blueprintId: " + blueprintId));
    }
}
