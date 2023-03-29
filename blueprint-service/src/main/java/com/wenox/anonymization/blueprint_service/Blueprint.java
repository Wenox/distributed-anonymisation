package com.wenox.anonymization.blueprint_service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Blueprint {

    @Id
    private final String id = UUID.randomUUID().toString();

    private RestoreMode restoreMode;

    private FileType type;

    private String title;

    private String blueprintDatabaseName;

    private String description;
    private LocalDateTime createdDate;
}