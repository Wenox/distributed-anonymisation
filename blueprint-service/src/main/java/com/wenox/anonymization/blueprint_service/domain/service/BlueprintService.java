package com.wenox.anonymization.blueprint_service.domain.service;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;

public interface BlueprintService {

    String importBlueprint(Blueprint blueprint);

    Blueprint getBlueprint(String blueprintId);
}
