package com.wenox.anonymization.blueprint_service.adapters.inbound.messaging;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.model.BlueprintInstantiatedEvent;
import com.wenox.anonymization.blueprint_service.domain.ports.DumpRepository;
import com.wenox.anonymization.blueprint_service.domain.ports.MessagePublisher;
import com.wenox.anonymization.blueprint_service.domain.service.BlueprintSagaStatusUpdater;
import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;
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

    private final BlueprintSagaStatusUpdater blueprintSagaStatusUpdater;
    private final DumpRepository dumpRepository;
    private final MessagePublisher messagePublisher;

    @KafkaListener(topics = KafkaConstants.TOPIC_CREATED_BLUEPRINT_LISTEN_TO_YOURSELF, groupId = "blueprint-service-group")
    void onBlueprintInstantiated(BlueprintInstantiatedEvent event, Acknowledgment ack) {
        log.info("Received {}", event);
        try {
            handleUploadAndStatus(event.getDump(), event.getBlueprint());
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to upload dump to Amazon S3 for event : {}", event);
            blueprintSagaStatusUpdater.updateSagaStatusOnDumpStoreFailure(event.getBlueprint());
        }
    }

    private void handleUploadAndStatus(byte[] content, Blueprint blueprint) {
        if (dumpRepository.uploadDump(content, blueprint)) {
            blueprintSagaStatusUpdater.updateSagaStatusOnDumpStoreSuccess(blueprint);
            messagePublisher.sendBlueprintCreated(new BlueprintCreatedEvent(blueprint.getBlueprintId(), blueprint.getRestoreMode()));
        } else {
            blueprintSagaStatusUpdater.updateSagaStatusOnDumpStoreFailure(blueprint);
        }
    }
}
