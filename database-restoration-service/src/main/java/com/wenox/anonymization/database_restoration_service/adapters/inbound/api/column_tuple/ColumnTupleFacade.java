package com.wenox.anonymization.database_restoration_service.adapters.inbound.api.column_tuple;

import com.wenox.anonymization.database_restoration_service.domain.model.ColumnTuple;
import com.wenox.anonymization.database_restoration_service.domain.service.column_tuple.ColumnTupleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ColumnTupleFacade {

    private final ColumnTupleService columnTupleService;

    ColumnTuple queryColumnTuple(String blueprintId, String table, String column, String pk) {
        return columnTupleService.queryColumnTuple(blueprintId, table, column, pk);
    }
}