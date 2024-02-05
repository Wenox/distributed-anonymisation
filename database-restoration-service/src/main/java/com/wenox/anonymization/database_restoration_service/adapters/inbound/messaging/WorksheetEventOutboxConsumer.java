package com.wenox.anonymization.database_restoration_service.adapters.inbound.messaging;

import com.wenox.anonymization.database_restoration_service.adapters.outbound.worksheet.WorksheetEventClient;
import com.wenox.anonymization.database_restoration_service.domain.model.WorksheetProjection;
import com.wenox.anonymization.database_restoration_service.domain.ports.WorksheetProjectionRepository;
import com.wenox.anonymization.database_restoration_service.domain.service.timestamp.TimestampService;
import com.wenox.anonymization.shared_chaos_library.api.ShutdownSimulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
class WorksheetEventOutboxConsumer {

    private final TimestampService timestampService;
    private final WorksheetProjectionRepository worksheetProjectionRepository;
    private final WorksheetEventClient worksheetEventClient;

    @Value("${SHOULD_CRASH:}")
    private boolean shouldCrash;

    @Scheduled(fixedRateString = "${transactional-outbox.worksheet-events.fetch-interval}")
    void processNewWorksheetEventsFromOutbox() {
        LocalDateTime timestamp = timestampService.getTimestamp();
        List<WorksheetProjection> worksheets = worksheetEventClient.fetchWorksheetEvents(timestamp).block();
        log.info("Processing worksheet events from outbox : {}", worksheets);
        worksheetProjectionRepository.saveAll(worksheets);
        if (shouldCrash && !worksheets.isEmpty()) {
            ShutdownSimulator.crashJVM("Crashing JVM before outbox timetamp is saved – processed messages will be redelivered");
        } else {
            timestampService.overrideTimestamp(LocalDateTime.now());
        }
    }
}
