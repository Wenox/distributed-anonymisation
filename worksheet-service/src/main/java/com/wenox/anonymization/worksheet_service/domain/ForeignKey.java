package com.wenox.anonymization.worksheet_service.domain;

public record ForeignKey(String referencingTableName,
                         String referencingColumnName,
                         String referencedTableName,
                         String referencedColumnName,
                         String foreignKeyName,
                         String primaryKeyName,
                         String type) {
}
