package com.wenox.anonymization.metadata_extraction_service;

import com.wenox.anonymization.metadata_extraction_service.domain.Metadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataService {

    private final MetadataRepository metadataRepository;

    public Metadata getMetadataByBlueprintId(String blueprintId) {
        return metadataRepository.findByBlueprintId(blueprintId).orElseThrow();
    }
}
