package com.wenox.anonymization.database_restoration_service.adapters.worksheet_db_adapter;

import com.wenox.anonymization.database_restoration_service.domain.model.WorksheetProjection;
import com.wenox.anonymization.database_restoration_service.domain.ports.WorksheetProjectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class WorksheetProjectionRepositoryAdapter implements WorksheetProjectionRepository {

    private final WorksheetProjectionEntityRepository worksheetProjectionEntityRepository;

    @Override
    public Optional<WorksheetProjection> findById(String worksheetId) {
        return worksheetProjectionEntityRepository.findById(worksheetId).map(WorksheetProjectionEntity::toDomain);
    }

    @Override
    public void save(WorksheetProjection projection) {
        WorksheetProjectionEntity entity = WorksheetProjectionEntity.fromDomain(projection);
        worksheetProjectionEntityRepository.save(entity);
    }
}
