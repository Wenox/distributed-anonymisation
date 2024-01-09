package com.wenox.anonymization.database_restoration_service.domain.service.column_tuple;

import com.wenox.anonymization.database_restoration_service.domain.exception.InactiveRestorationException;
import com.wenox.anonymization.database_restoration_service.domain.model.Restoration;
import com.wenox.anonymization.database_restoration_service.domain.model.ColumnTuple;
import com.wenox.anonymization.database_restoration_service.domain.ports.ColumnTuplePort;
import com.wenox.anonymization.database_restoration_service.domain.service.restoration.RestorationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DefaultColumnTupleService implements ColumnTupleService {

    private final RestorationService restorationService;
    private final ColumnTuplePort columnTuplePort;

    // todo: !! define ColumnTupleRequest { blueprintId, table, column, pk }
    @Override
    public ColumnTuple queryColumnTuple(String blueprintId, String table, String column, String pk) {
        Restoration restoration = restorationService.getRestorationByBlueprintId(blueprintId);
        if (!restoration.isActive()) {
            throw new InactiveRestorationException("Unable to get column data: Restoration is inactive! Restoration: " + restoration);
        }

        return columnTuplePort.fetchColumnTuple(restoration.getBlueprintId(), table, column, pk);
    }
}
