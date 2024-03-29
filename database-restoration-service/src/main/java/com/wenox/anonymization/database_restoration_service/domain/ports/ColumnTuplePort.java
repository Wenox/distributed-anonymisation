package com.wenox.anonymization.database_restoration_service.domain.ports;

import com.wenox.anonymization.database_restoration_service.domain.model.ColumnTuple;

public interface ColumnTuplePort {

    ColumnTuple fetchColumnTuple(String db, String tableName, String primaryKeyColumnName, String columnName);
}
