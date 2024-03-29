package com.wenox.anonymization.worksheet_service;

import com.wenox.anonymization.shared_events_library.api.RestoreMode;
import com.wenox.anonymization.worksheet_service.domain.Metadata;
import com.wenox.anonymization.worksheet_service.domain.WorksheetStatus;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
public class WorksheetResponse {

    private String worksheetId;

    private String blueprintId;

    private WorksheetStatus worksheetStatus;

    private String worksheetName;

    private RestoreMode restoreMode;

    private Metadata metadata;
}
