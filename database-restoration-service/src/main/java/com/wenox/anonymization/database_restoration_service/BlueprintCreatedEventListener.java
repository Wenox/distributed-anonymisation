package com.wenox.anonymization.database_restoration_service;

import com.wenox.anonymization.s3_file_manager.KafkaConstants;
import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BlueprintCreatedEventListener {

    @KafkaListener(topics = KafkaConstants.TOPIC_BLUEPRINTS, groupId = "wenox")
    public void onBlueprintCreated(BlueprintCreatedEvent event) {
        log.info("Received: " + event);
        log.info("Exiting :-)");
    }
}
