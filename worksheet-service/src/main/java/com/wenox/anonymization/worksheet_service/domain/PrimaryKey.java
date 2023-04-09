package com.wenox.anonymization.worksheet_service.domain;

public record PrimaryKey(String primaryKeyName,
                         String columnName,
                         String type,
                         boolean nullable,
                         boolean primaryKey,
                         boolean foreignKey) {
}
