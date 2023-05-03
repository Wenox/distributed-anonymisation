package com.wenox.anonymization.database_restoration_service.worksheet;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorksheetProjectionRepository extends MongoRepository<WorksheetProjection, String> {
}
