package com.wenox.anonymization.blueprint_service.adapters.api;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.service.BlueprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlueprintFacade {

    private final BlueprintService blueprintService;
    private final BlueprintMapper blueprintMapper;

    public String importBlueprint(ImportBlueprintRequest dto) {
        Blueprint blueprint = blueprintMapper.fromRequest(dto);
        return blueprintService.importBlueprint(blueprint);
    }

    public Blueprint getBlueprint(String id) {
        return blueprintService.getBlueprint(id);
    }
}
