package com.wenox.anonymization.database_restoration_service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestorationRepository extends MongoRepository<Restoration, String> {
}
