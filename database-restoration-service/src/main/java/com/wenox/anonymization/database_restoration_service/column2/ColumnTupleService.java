package com.wenox.anonymization.database_restoration_service.column2;

public interface ColumnTupleService {

    ColumnTuple queryColumnTuple(String blueprintId, String table, String column, String pk);
}
