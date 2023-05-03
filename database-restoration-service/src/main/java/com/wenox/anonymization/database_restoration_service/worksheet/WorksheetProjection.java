package com.wenox.anonymization.database_restoration_service.worksheet;

import com.wenox.anonymization.shared_events_library.api.RestoreMode;
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
public class WorksheetProjection {

    @Id
    private String worksheetId;

    private String blueprintId;

    private String databaseName;

    private RestoreMode restoreMode;
}

