package com.wenox.anonymization.metadata_extraction_service.adapters.outbound.persistence.metadata;

import com.wenox.anonymization.metadata_extraction_service.domain.model.Metadata;
import com.wenox.anonymization.metadata_extraction_service.domain.model.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document
public class MetadataEntity {

    @Id
    private String blueprintId;

    private int numberOfTables;

    private Map<String, Table> tables;

    static MetadataEntity fromDomain(Metadata metadata) {
        MetadataEntity entity = new MetadataEntity();
        entity.setBlueprintId(metadata.getBlueprintId());
        entity.setNumberOfTables(metadata.getNumberOfTables());
        entity.setTables(metadata.getTables());
        return entity;
    }

    Metadata toDomain() {
        Metadata metadata = new Metadata();
        metadata.setBlueprintId(this.getBlueprintId());
        metadata.setNumberOfTables(this.getNumberOfTables());
        metadata.setTables(this.getTables());
        return metadata;
    }
}
