package com.wenox.anonymization.blueprint_service.adapters.inbound.api;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.model.BlueprintSagaStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class BlueprintForDashboard {
    private String blueprintId;
    private BlueprintSagaStatus status;
    private String title;

    public static BlueprintForDashboard from(Blueprint blueprint) {
        return new BlueprintForDashboard(blueprint.getBlueprintId(), blueprint.getBlueprintSagaStatus(), blueprint.getTitle());
    }
}
