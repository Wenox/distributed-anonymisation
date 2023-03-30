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
    private String id;

    private RestoreMode restoreMode;

    private FileType type;

    private String title;

    private String blueprintDatabaseName;

    private String description;
    private LocalDateTime createdDate;

    private String savedDumpName;

    private String originalDumpName;

    private String contentType;
    private byte[] fileContent;
}