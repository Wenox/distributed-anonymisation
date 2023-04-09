package com.wenox.anonymization.worksheet_service.domain;

import java.util.Map;

public record Table(String tableName,
                    Integer numberOfRows,
                    Integer numberOfColumns,
                    Map<String, Column> columns,
                    PrimaryKey primaryKey) {
}
