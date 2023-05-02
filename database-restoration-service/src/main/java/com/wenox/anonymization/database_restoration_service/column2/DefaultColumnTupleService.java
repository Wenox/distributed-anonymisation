package com.wenox.anonymization.database_restoration_service.column2;

import com.wenox.anonymization.database_restoration_service.InactiveRestorationException;
import com.wenox.anonymization.database_restoration_service.Restoration;
import com.wenox.anonymization.database_restoration_service.RestorationService;
import com.wenox.anonymization.database_restoration_service.column2.connection.DataSourceFactory;
import com.wenox.anonymization.database_restoration_service.column2.connection.DatabaseConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultColumnTupleService implements ColumnTupleService {

    private final RestorationService restorationService;
    private final DataSourceFactory dataSourceFactory;

    @Override
    public ColumnTuple queryColumnTuple(String blueprintId, String table, String column, String pk) {
        Restoration restoration = restorationService.getRestorationByBlueprintId(blueprintId);
        if (!restoration.isActive()) {
            throw new InactiveRestorationException("Unable to get column data: Restoration is inactive! Restoration: " + restoration);
        }

        DatabaseConnection connection = DatabaseConnection.forPostgres(restoration.getDatabaseName());
        QuerySelector querySelector = new JdbcTemplateQuerySelector(dataSourceFactory.getDataSource(connection));

        log.info("Querying TABLE: {}, COLUMN: {}, PK: {}, DATABASE: {}", table, column, pk, restoration.getDatabaseName());
        return querySelector.select(table, pk, column);
    }
}
