package com.wenox.anonymization.database_restoration_service.domain.service.restoration;

import com.wenox.anonymization.database_restoration_service.domain.model.Restoration;
import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;

public interface RestorationService {

    Restoration getRestorationByBlueprintId(String blueprintId);

    void saveActiveRestoration(BlueprintCreatedEvent event);

    void saveInactiveRestoration(BlueprintCreatedEvent event);
}
