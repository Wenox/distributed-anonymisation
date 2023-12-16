package com.wenox.anonymization.database_restoration_service.domain.service.restoration;

import com.wenox.anonymization.database_restoration_service.domain.ports.RestorationRepository;
import com.wenox.anonymization.database_restoration_service.domain.exception.RestorationNotFoundException;
import com.wenox.anonymization.database_restoration_service.domain.model.Restoration;
import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultRestorationService implements RestorationService {

    private final RestorationRepository restorationRepository;

    public Restoration getRestorationByBlueprintId(String blueprintId) {
        return restorationRepository.findByBlueprintId(blueprintId)
                .orElseThrow(() -> new RestorationNotFoundException("Restoration not found with blueprintId: " + blueprintId));
    }

    public void saveActiveRestoration(BlueprintCreatedEvent event) {
        restorationRepository.save(Restoration.toActiveRestoration(event));
    }

    public void saveInactiveRestoration(BlueprintCreatedEvent event) {
        restorationRepository.save(Restoration.toInactiveRestoration(event));
    }
}
