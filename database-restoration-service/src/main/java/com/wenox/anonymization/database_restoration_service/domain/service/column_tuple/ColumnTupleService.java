package com.wenox.anonymization.database_restoration_service.domain.service.column_tuple;

import com.wenox.anonymization.database_restoration_service.domain.model.ColumnTuple;

public interface ColumnTupleService {

    ColumnTuple queryColumnTuple(String blueprintId, String table, String column, String pk);
}
