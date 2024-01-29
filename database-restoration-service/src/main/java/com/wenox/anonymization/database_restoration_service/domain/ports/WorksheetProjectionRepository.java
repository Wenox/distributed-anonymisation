package com.wenox.anonymization.database_restoration_service.domain.ports;


import com.wenox.anonymization.database_restoration_service.domain.model.WorksheetProjection;

import java.util.List;
import java.util.Optional;

public interface WorksheetProjectionRepository {

    Optional<WorksheetProjection> findById(String worksheetId);

    void save(WorksheetProjection projection);

    void saveAll(List<WorksheetProjection> projection);
}
