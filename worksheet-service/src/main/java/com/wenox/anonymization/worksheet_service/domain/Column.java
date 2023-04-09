package com.wenox.anonymization.worksheet_service.domain;

public record Column(String columnName,
                     String type,
                     boolean nullable,
                     boolean primaryKey,
                     boolean foreignKey) {
}
