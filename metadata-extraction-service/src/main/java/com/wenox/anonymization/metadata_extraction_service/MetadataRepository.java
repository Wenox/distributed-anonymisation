package com.wenox.anonymization.metadata_extraction_service;

import com.wenox.anonymization.metadata_extraction_service.domain.Metadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetadataRepository extends MongoRepository<Metadata, String> {

    Optional<Metadata> findByBlueprintId(String blueprintId);
}
