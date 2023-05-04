package com.wenox.anonymization.database_restoration_service.mirror;

import com.wenox.anonymization.database_restoration_service.RestorationDelegate;
import com.wenox.anonymization.database_restoration_service.RestorationService;
import com.wenox.anonymization.database_restoration_service.WorksheetNotFoundException;
import com.wenox.anonymization.database_restoration_service.worksheet.WorksheetProjection;
import com.wenox.anonymization.database_restoration_service.worksheet.WorksheetProjectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultMirrorService {

    private final WorksheetProjectionRepository worksheetRepository;
    private final RestorationDelegate restorationDelegate;
    private final RestorationService restorationService;

    public CreateMirrorResponse createMirror(CreateMirrorRequest dto) {
        WorksheetProjection worksheet = worksheetRepository.findById(dto.getWorksheetId())
                .orElseThrow(() -> new WorksheetNotFoundException("Worksheet not found with id: " + dto.getWorksheetId()));

        try {
            restorationDelegate.restore("mirror-" + worksheet.getDatabaseName(), worksheet.getRestoreMode());
        } catch (Exception ex) {
            log.error("Error during database restoration for dto : {}, worksheet: {}", dto, worksheet, ex);
        }

        // todo: add Either failure

        return new CreateMirrorResponse("mirror-" + worksheet.getDatabaseName());
    }
}
