package com.wenox.anonymization.blueprint_service;

import com.wenox.anonymization.shared_events_library.api.RestoreMode;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document
public class Blueprint {

    @Id
    private String blueprintId;

    private BlueprintStatus blueprintStatus;

    private RestoreMode restoreMode;

    private DatabaseType databaseType;

    private String title;

    private String blueprintDatabaseName;

    private boolean dumpStoreSuccess;

    private String description;
    private LocalDateTime createdDate;

    private String savedDumpName;

    private String originalDumpName;
}