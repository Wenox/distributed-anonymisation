package com.wenox.anonymization.blueprint_service.domain.ports;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface BlueprintRepository {

    Blueprint save(Blueprint blueprint);

    Optional<Blueprint> findById(String id);

    List<Blueprint> fetchStaleBlueprints(LocalDateTime thresholdTime);

    List<Blueprint> saveAll(List<Blueprint> blueprints);

    List<Blueprint> getBlueprintsForDashboard();
}
