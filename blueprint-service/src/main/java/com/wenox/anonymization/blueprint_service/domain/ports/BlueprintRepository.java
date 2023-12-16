package com.wenox.anonymization.blueprint_service.domain.ports;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;

import java.util.Optional;

public interface BlueprintRepository {

    Blueprint save(Blueprint blueprint);

    Optional<Blueprint> findById(String id);
}
