package com.wenox.anonymization.database_restoration_service.adapters.api;

import com.wenox.anonymization.database_restoration_service.domain.model.Restoration;
import com.wenox.anonymization.database_restoration_service.domain.service.restoration.DefaultRestorationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class RestorationFacade {

    private final DefaultRestorationService restorationService;

    Restoration getRestorationByBlueprintId(String blueprintId) {
        return restorationService.getRestorationByBlueprintId(blueprintId);
    }
}