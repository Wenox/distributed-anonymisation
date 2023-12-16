package com.wenox.anonymization.database_restoration_service.adapters.outbound.persistence.worksheet;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface WorksheetProjectionEntityRepository extends MongoRepository<WorksheetProjectionEntity, String> {
}
