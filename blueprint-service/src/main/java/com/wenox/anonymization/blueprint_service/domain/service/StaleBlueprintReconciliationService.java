package com.wenox.anonymization.blueprint_service.domain.service;

import com.wenox.anonymization.blueprint_service.domain.model.Blueprint;
import com.wenox.anonymization.blueprint_service.domain.ports.BlueprintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class StaleBlueprintReconciliationService {

    @Value("${importing.stale-blueprints.threshold-seconds}")
    private long thresholdSeconds;

    private final BlueprintRepository blueprintRepository;

    @Scheduled(cron = "${importing.stale-blueprints.cron}")
    public void detectStaleBlueprints() {
        log.info("Stale blueprints reconciliation started...");
        LocalDateTime thresholdTime = LocalDateTime.now().minusSeconds(thresholdSeconds);
        Stream<Blueprint> staleBlueprints = blueprintRepository.fetchStaleBlueprints(thresholdTime);
        staleBlueprints.forEach(Blueprint::toStale);
        blueprintRepository.saveAll(staleBlueprints);
        log.info("Stale blueprints reconciliation ended. Reconciled blueprints : {} before time : {}", staleBlueprints, thresholdTime);
    }
}
