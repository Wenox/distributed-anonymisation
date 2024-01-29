package com.wenox.anonymization.blueprint_service.adapters.outbound.messaging;

import com.wenox.anonymization.blueprint_service.domain.model.BlueprintInstantiatedEvent;
import com.wenox.anonymization.blueprint_service.domain.ports.MessagePublisher;
import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.shared_events_library.api.KafkaTemplateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MessagePublisherKafkaAdapter implements MessagePublisher {

    private final KafkaTemplateWrapper<String, Object> loggingKafkaTemplate;

    @Override
    public void sendBlueprintCreated(BlueprintCreatedEvent event) {
        loggingKafkaTemplate.send(KafkaConstants.TOPIC_CREATED_BLUEPRINT, event);
    }

    @Override
    public void sendBlueprintInstantiated(BlueprintInstantiatedEvent event) {
        loggingKafkaTemplate.send(KafkaConstants.TOPIC_CREATED_BLUEPRINT_LISTEN_TO_YOURSELF, event);
    }
}
