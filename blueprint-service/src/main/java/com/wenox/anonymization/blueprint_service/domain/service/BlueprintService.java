package com.wenox.anonymization.blueprint_service.domain.service;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;

import java.util.List;

public interface BlueprintService {

    String importBlueprint(Blueprint blueprint);

    Blueprint getBlueprint(String blueprintId);

    List<Blueprint> getBlueprintsForDashboard();
}
