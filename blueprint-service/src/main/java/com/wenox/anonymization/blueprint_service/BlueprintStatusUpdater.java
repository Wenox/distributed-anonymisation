package com.wenox.anonymization.blueprint_service;

import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.shared_events_library.api.KafkaTemplateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlueprintStatusUpdater {
    private final BlueprintRepository blueprintRepository;
    private final KafkaTemplateWrapper<String, Object> loggingKafkaTemplate;

    public void updateBlueprintStatusOnSuccess(Blueprint blueprint) {
        blueprint.setDumpStoreSuccess(true);
        blueprint.setBlueprintStatus(BlueprintStatus.DUMP_STORE_SUCCESS);
        blueprintRepository.save(blueprint);
        loggingKafkaTemplate.send(KafkaConstants.TOPIC_BLUEPRINTS, new BlueprintCreatedEvent(blueprint.getBlueprintId(), blueprint.getBlueprintDatabaseName()));
    }

    public void updateBlueprintStatusOnFailure(Blueprint blueprint) {
        blueprint.setBlueprintStatus(BlueprintStatus.DUMP_STORE_FAILURE);
        blueprint.setDumpStoreSuccess(false);
        blueprintRepository.save(blueprint);
    }
}