package com.wenox.anonymization.worksheet_service.domain;

import com.wenox.anonymization.shared_events_library.api.RestoreMode;

import java.time.LocalDateTime;

public record Blueprint(String blueprintId,
                        BlueprintStatus blueprintStatus,
                        RestoreMode restoreMode,
                        DatabaseType databaseType,
                        String title,
                        boolean dumpStoreSuccess,
                        String description,
                        LocalDateTime createdDate,
                        String originalDumpName) {

}
