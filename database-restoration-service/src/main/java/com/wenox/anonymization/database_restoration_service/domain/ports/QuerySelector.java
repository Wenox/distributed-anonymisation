package com.wenox.anonymization.database_restoration_service.domain.ports;

import com.wenox.anonymization.database_restoration_service.domain.model.ColumnTuple;

public interface QuerySelector {

    ColumnTuple select(String tableName, String primaryKeyColumnName, String columnName);
}