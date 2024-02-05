package com.wenox.anonymization.blueprint_service.adapters.inbound.messaging;

import com.wenox.anonymization.blueprint_service.domain.model.BlueprintInstantiatedEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlueprintInstantiatedKafkaListener {

    @KafkaListener(topics = KafkaConstants.TOPIC_CREATED_BLUEPRINT_LISTEN_TO_YOURSELF, groupId = "blueprint-service-group")
    void onBlueprintInstantiated(BlueprintInstantiatedEvent event, Acknowledgment ack) {
        log.info("Received {}", event);
        throw new RuntimeException("Throwing runtime exception");
    }
}
