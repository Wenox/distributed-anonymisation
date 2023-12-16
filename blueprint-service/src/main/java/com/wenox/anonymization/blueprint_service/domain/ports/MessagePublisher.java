package com.wenox.anonymization.blueprint_service.domain.ports;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;

public interface MessagePublisher {

    void sendBlueprintCreated(Blueprint blueprint);
}
