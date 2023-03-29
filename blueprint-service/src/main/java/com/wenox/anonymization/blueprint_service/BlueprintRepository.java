package com.wenox.anonymization.blueprint_service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlueprintRepository extends MongoRepository<Blueprint, String> {
}
