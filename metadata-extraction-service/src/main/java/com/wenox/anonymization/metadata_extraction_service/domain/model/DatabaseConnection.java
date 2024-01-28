package com.wenox.anonymization.metadata_extraction_service.domain.model;

import lombok.Value;

@Value
public class DatabaseConnection {

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

    public static DatabaseConnection forPostgres(String db) {
        throw new RuntimeException("Simulating a failure during metadata extraction operation");
    }
}
