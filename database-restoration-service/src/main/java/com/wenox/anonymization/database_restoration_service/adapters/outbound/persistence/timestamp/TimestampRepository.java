package com.wenox.anonymization.database_restoration_service.adapters.outbound.persistence.timestamp;

import com.wenox.anonymization.database_restoration_service.domain.model.Timestamp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimestampRepository extends MongoRepository<Timestamp, String> {
}