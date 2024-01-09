package com.wenox.anonymization.blueprint_service.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wenox.anonymization.shared_events_library.api.RestoreMode;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Blueprint {

    private String blueprintId;

    @JsonIgnore
    private MultipartFile dumpFile;

    private BlueprintSagaStatus blueprintSagaStatus;

    private RestoreMode restoreMode;

    private DatabaseType databaseType;

    private String title;

    private String blueprintDatabaseName;

    private boolean dumpStoreSuccess;

    private String description;

    private LocalDateTime createdDate;

    private String originalDumpName;

    public Blueprint toStale() {
        this.blueprintSagaStatus = BlueprintSagaStatus.STALE;
        return this;
    }
}