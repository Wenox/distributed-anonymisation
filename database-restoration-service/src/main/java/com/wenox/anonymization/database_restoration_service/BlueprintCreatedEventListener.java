package com.wenox.anonymization.database_restoration_service;

import com.wenox.anonymization.s3_file_manager.KafkaConstants;
import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlueprintCreatedEventListener {

    private final RestoreFacade restoreFacade;

    @KafkaListener(topics = KafkaConstants.TOPIC_BLUEPRINTS, groupId = "wenox")
    public void onBlueprintCreated(BlueprintCreatedEvent event) throws IOException, InterruptedException, TimeoutException {
        log.info("Received: " + event);
        restoreFacade.restore(event.getDatabaseName(), RestoreMode.ARCHIVE);
        log.info("Exiting :-)");
    }
}
