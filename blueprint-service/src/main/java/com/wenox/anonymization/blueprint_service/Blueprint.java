package com.wenox.anonymization.blueprint_service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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