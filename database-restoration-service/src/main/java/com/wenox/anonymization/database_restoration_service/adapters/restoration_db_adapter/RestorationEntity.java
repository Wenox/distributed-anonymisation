package com.wenox.anonymization.database_restoration_service.adapters.restoration_db_adapter;

import com.wenox.anonymization.database_restoration_service.domain.model.Restoration;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
@Document
class RestorationEntity {

    @Id
    private String restorationId;

    private String blueprintId;

    private String databaseName;

    private boolean isActive = false;

    private String runnerIp;

    Restoration toDomain() {
        Restoration restoration = new Restoration();
        restoration.setRestorationId(this.getRestorationId());
        restoration.setBlueprintId(this.getBlueprintId());
        restoration.setDatabaseName(this.getDatabaseName());
        restoration.setActive(this.isActive());
        restoration.setRunnerIp(this.getRunnerIp());
        return restoration;
    }

    static RestorationEntity fromDomain(Restoration restoration) {
        RestorationEntity entity = new RestorationEntity();
        entity.setRestorationId(restoration.getRestorationId());
        entity.setBlueprintId(restoration.getBlueprintId());
        entity.setDatabaseName(restoration.getDatabaseName());
        entity.setActive(restoration.isActive());
        entity.setRunnerIp(restoration.getRunnerIp());
        return entity;
    }
}
