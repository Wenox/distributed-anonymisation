package com.wenox.anonymization.blueprint_service.adapters.outbound.persistence.blueprint;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
interface BlueprintEntityRepository extends MongoRepository<BlueprintEntity, String> {

    List<BlueprintEntity> findByCreatedDateBefore(LocalDateTime thresholdTime);
}
