package com.wenox.anonymization.worksheet_service.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document
public class Worksheet {

    @Id
    private String worksheetId;

    private String blueprintId;

    private String worksheetName;

    private String databaseName;

    private Metadata metadata;

    private WorksheetStatus worksheetStatus;
}
