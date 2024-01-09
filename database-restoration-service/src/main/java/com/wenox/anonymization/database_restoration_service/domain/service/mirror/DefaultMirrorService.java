package com.wenox.anonymization.database_restoration_service.domain.service.mirror;

import com.wenox.anonymization.database_restoration_service.domain.exception.MirrorCreationException;
import com.wenox.anonymization.database_restoration_service.domain.service.restoration_lifecycle.RestorationLifecycleService;
import com.wenox.anonymization.database_restoration_service.domain.exception.WorksheetNotFoundException;
import com.wenox.anonymization.database_restoration_service.domain.model.WorksheetProjection;
import com.wenox.anonymization.database_restoration_service.domain.ports.WorksheetProjectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DefaultMirrorService implements MirrorService {

    private final WorksheetProjectionRepository worksheetProjectionRepository;
    private final RestorationLifecycleService restorationLifecycleService;

    public String createMirror(String worksheetId) {
        WorksheetProjection worksheet = worksheetProjectionRepository.findById(worksheetId)
                .orElseThrow(() -> new WorksheetNotFoundException("Worksheet not found with id: " + worksheetId));

        final String mirrorDb = "mirror-" + worksheet.getBlueprintId();

        try {
            restorationLifecycleService.restore(mirrorDb, worksheet.getRestoreMode());
        } catch (Exception ex) {
            log.error("Error creating Mirror database for worksheet : {}", worksheet, ex);
            throw new MirrorCreationException("Error creating Mirror database for worksheet : " + worksheet, ex);
        }

        return mirrorDb;
    }
}
