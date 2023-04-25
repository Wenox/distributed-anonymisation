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
    public ColumnTuple queryColumnTuple(String blueprintId, String table, String column) {
        Restoration restoration = restorationService.getRestorationByBlueprintId(blueprintId);
        if (!restoration.isActive()) {
            throw new InactiveRestorationException("Unable to get column data: Restoration is inactive! Restoraion: " + restoration);
        }

        log.info("Querying db: {}", restoration.getDatabaseName());

        DatabaseConnection connection = DatabaseConnection.forPostgres(restoration.getDatabaseName());
        QuerySelector querySelector = new JdbcTemplateQuerySelector(dataSourceFactory.getDataSource(connection));

        log.info("Querying table: {}, column: {}, pk: {}", table, column, "id");
        return querySelector.select(table, "id", column);
    }
}
