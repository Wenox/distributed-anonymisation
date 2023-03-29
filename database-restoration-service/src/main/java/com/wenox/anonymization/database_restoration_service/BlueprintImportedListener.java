package com.wenox.anonymization.database_restoration_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BlueprintImportedListener {

    @KafkaListener(topics = "wenox-blueprints", groupId = "wenox")
    public void listenGroupFoo(String message) {
        System.out.println("\n\n\n\nReceived Message in group foo: " + message);
        log.info("Exiting :-)");
    }
}
