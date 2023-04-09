package com.wenox.anonymization.database_restoration_service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestorationRepository extends MongoRepository<Restoration, String> {

    Optional<Restoration> findByBlueprintId(String blueprintId);
}
