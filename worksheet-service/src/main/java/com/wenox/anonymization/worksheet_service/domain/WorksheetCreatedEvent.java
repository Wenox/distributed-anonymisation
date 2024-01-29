package com.wenox.anonymization.worksheet_service.domain;

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
public class WorksheetCreatedEvent {

    @Id
    private String worksheetId;

    private String blueprintId;

    private RestoreMode restoreMode;

    private LocalDateTime createdAt;
}
