package com.wenox.anonymization.metadata_extraction_service.adapters.outbound.persistence.metadata;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetadataEntityRepository extends MongoRepository<MetadataEntity, String> {

    Optional<MetadataEntity> findByBlueprintId(String blueprintId);
}
