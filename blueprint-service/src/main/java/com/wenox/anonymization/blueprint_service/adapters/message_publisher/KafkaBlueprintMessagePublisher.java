package com.wenox.anonymization.blueprint_service.adapters.message_publisher;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.ports.BlueprintMessagePublisher;
import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.shared_events_library.api.KafkaTemplateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaBlueprintMessagePublisher implements BlueprintMessagePublisher {

    private final KafkaTemplateWrapper<String, Object> loggingKafkaTemplate;

    @Override
    public void sendBlueprintCreated(Blueprint blueprint) {
        loggingKafkaTemplate.send(KafkaConstants.TOPIC_CREATED_BLUEPRINT, new BlueprintCreatedEvent(blueprint.getBlueprintId(), blueprint.getBlueprintDatabaseName(), blueprint.getRestoreMode()));
    }
}
