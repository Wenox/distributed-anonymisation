package com.wenox.anonymization.database_restoration_service.adapters.restoration_db_adapter;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
interface RestorationEntityRepository extends MongoRepository<RestorationEntity, String> {

    Optional<RestorationEntity> findByBlueprintId(String blueprintId);
}
