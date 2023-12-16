package com.wenox.anonymization.database_restoration_service.adapters.column_tuple_adapter;

import com.wenox.anonymization.database_restoration_service.domain.model.DatabaseType;
import lombok.Value;

@Value
class DatabaseConnection {

    DatabaseType databaseType;
    String databaseName;
    String username;
    String password;

    private DatabaseConnection(DatabaseType databaseType, String databaseName, String username, String password) {
        this.databaseType = databaseType;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    static DatabaseConnection forPostgres(String databaseName) {
        return new DatabaseConnection(DatabaseType.POSTGRESQL, databaseName, "postgres", "postgres");
    }
}
