package com.wenox.anonymization.database_restoration_service.adapters.outbound.persistence.restoration;

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
    private String blueprintId;

    private String databaseName;

    private boolean isActive = false;

    private String runnerIp;

    Restoration toDomain() {
        Restoration restoration = new Restoration();
        restoration.setBlueprintId(this.getBlueprintId());
        restoration.setDatabaseName(this.getDatabaseName());
        restoration.setActive(this.isActive());
        restoration.setRunnerIp(this.getRunnerIp());
        return restoration;
    }

    static RestorationEntity fromDomain(Restoration restoration) {
        RestorationEntity entity = new RestorationEntity();
        entity.setBlueprintId(restoration.getBlueprintId());
        entity.setDatabaseName(restoration.getDatabaseName());
        entity.setActive(restoration.isActive());
        entity.setRunnerIp(restoration.getRunnerIp());
        return entity;
    }
}
