package com.wenox.anonymization.blueprint_service.adapters.outbound.persistence.blueprint;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface BlueprintEntityRepository extends MongoRepository<BlueprintEntity, String> {
}
