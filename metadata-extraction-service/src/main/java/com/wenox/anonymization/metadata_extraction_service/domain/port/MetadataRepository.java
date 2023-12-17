package com.wenox.anonymization.metadata_extraction_service.domain.port;

import com.wenox.anonymization.metadata_extraction_service.domain.model.Metadata;

import java.util.Optional;

public interface MetadataRepository {

    Optional<Metadata> findByBlueprintId(String blueprintId);

    void save(Metadata metadata);
}
