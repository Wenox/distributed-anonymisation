package com.wenox.anonymization.blueprint_service.adapters.inbound.api;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.service.BlueprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class BlueprintFacade {

    private final BlueprintService blueprintService;
    private final BlueprintMapper blueprintMapper;

    String importBlueprint(ImportBlueprintRequest dto) {
        Blueprint blueprint = blueprintMapper.fromRequest(dto);
        return blueprintService.importBlueprint(blueprint);
    }

    Blueprint getBlueprint(String id) {
        return blueprintService.getBlueprint(id);
    }

    List<BlueprintForDashboard> getBlueprintsForDashboard() {
        return blueprintService.getBlueprintsForDashboard()
                .stream()
                .map(BlueprintForDashboard::from)
                .toList();
    }
}
