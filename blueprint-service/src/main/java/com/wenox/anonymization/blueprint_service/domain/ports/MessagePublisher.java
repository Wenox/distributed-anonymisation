package com.wenox.anonymization.blueprint_service.domain.ports;

import com.wenox.anonymization.blueprint_service.domain.model.BlueprintInstantiatedEvent;
import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;

public interface MessagePublisher {

    void sendBlueprintCreated(BlueprintCreatedEvent event);
    void sendBlueprintInstantiated(BlueprintInstantiatedEvent event);
}
