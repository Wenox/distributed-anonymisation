package com.wenox.anonymization.database_restoration_service.adapters.out.column_tuple;

import com.wenox.anonymization.database_restoration_service.domain.model.ColumnTuple;
import com.wenox.anonymization.database_restoration_service.domain.ports.QuerySelector;
import com.wenox.anonymization.database_restoration_service.domain.ports.ColumnTuplePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class ColumnTupleAdapter implements ColumnTuplePort {

    private final DataSourceFactory dataSourceFactory;

    @Override
    public ColumnTuple fetchColumnTuple(String databaseName, String table, String pk, String column) {
        DatabaseConnection connection = DatabaseConnection.forPostgres(databaseName);
        QuerySelector querySelector = new JdbcTemplateQuerySelector(dataSourceFactory.getDataSource(connection));
        return querySelector.select(table, pk, column);
    }
}
