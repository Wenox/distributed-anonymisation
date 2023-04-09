package com.wenox.anonymization.database_restoration_service;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document
@Builder(toBuilder = true)
@AllArgsConstructor
public class Restoration {

    @Id
    private String restorationId;

    private String blueprintId;

    private String databaseName;

    private boolean isActive = false;

    private String runnerIp;
}
