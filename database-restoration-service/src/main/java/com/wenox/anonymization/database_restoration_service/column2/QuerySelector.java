package com.wenox.anonymization.database_restoration_service.column2;

public interface QuerySelector {

    ColumnTuple select(String tableName, String primaryKeyColumnName, String columnName);
}