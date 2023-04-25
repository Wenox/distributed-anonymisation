package com.wenox.anonymization.database_restoration_service.column2.connection;

import lombok.Value;

@Value
public class DatabaseConnection {

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

    public static DatabaseConnection forPostgres(String databaseName) {
        return new DatabaseConnection(DatabaseType.POSTGRESQL, databaseName, "postgres", "postgres");
    }
}
