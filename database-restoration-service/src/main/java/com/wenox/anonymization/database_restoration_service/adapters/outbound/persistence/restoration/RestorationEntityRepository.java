package com.wenox.anonymization.database_restoration_service.adapters.outbound.persistence.restoration;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RestorationEntityRepository extends MongoRepository<RestorationEntity, String> {

}
