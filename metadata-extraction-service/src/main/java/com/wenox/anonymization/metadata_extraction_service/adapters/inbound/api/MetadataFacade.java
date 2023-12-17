package com.wenox.anonymization.metadata_extraction_service.adapters.inbound.api;

import com.wenox.anonymization.metadata_extraction_service.domain.model.Metadata;
import com.wenox.anonymization.metadata_extraction_service.domain.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetadataFacade {

    private final MetadataService metadataService;

    Metadata getMetadataByBlueprintId(String blueprintId) {
        return metadataService.getMetadataByBlueprintId(blueprintId);
    }
}
