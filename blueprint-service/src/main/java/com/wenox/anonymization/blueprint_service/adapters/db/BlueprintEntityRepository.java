package com.wenox.anonymization.blueprint_service.adapters.db;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface BlueprintEntityRepository extends MongoRepository<BlueprintEntity, String> {
}
