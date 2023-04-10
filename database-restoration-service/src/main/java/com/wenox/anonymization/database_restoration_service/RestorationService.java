package com.wenox.anonymization.database_restoration_service;

import com.wenox.anonymization.shared_events_library.BlueprintCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RestorationService {

    private final RestorationRepository restorationRepository;
    private final RestorationMapper restorationMapper;

    public Restoration getRestorationByBlueprintId(String blueprintId) {
        return restorationRepository.findByBlueprintId(blueprintId)
                .orElseThrow(() -> new RestorationNotFoundException("Restoration not found with blueprintId: " + blueprintId));
    }

    public void saveActiveRestoration(BlueprintCreatedEvent event) {
        restorationRepository.save(restorationMapper.toActiveRestoration(event));
    }

    public void saveInactiveRestoration(BlueprintCreatedEvent event) {
        restorationRepository.save(restorationMapper.toInactiveRestoration(event));
    }
}
