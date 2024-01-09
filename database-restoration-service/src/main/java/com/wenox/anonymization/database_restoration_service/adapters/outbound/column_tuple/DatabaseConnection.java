package com.wenox.anonymization.database_restoration_service.adapters.outbound.column_tuple;

import com.wenox.anonymization.database_restoration_service.domain.model.DatabaseType;
import lombok.Value;

@Value
class DatabaseConnection {

    DatabaseType databaseType;
    String db;
    String username;
    String password;

    private DatabaseConnection(DatabaseType databaseType, String db, String username, String password) {
        this.databaseType = databaseType;
        this.db = db;
        this.username = username;
        this.password = password;
    }

    static DatabaseConnection forPostgres(String db) {
        return new DatabaseConnection(DatabaseType.POSTGRESQL, db, "postgres", "postgres");
    }
}
