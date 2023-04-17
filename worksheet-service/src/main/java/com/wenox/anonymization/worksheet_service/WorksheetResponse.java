package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.worksheet_service.domain.Metadata;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
public class WorksheetResponse {

    private String worksheetId;

    private String blueprintId;

    private String worksheetName;

    private String databaseName;

    private Metadata metadata;
}
