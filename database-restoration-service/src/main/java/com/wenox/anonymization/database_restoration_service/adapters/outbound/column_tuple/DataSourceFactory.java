package com.wenox.anonymization.database_restoration_service.adapters.outbound.column_tuple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
@RequiredArgsConstructor
class DataSourceFactory {

    private final ColumnTupleDatabaseConnectionConfig lifecycleConfig;

    DataSource getDataSource(DatabaseConnection databaseConnection) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        switch (databaseConnection.getDatabaseType()) {
            case POSTGRESQL -> {
                dataSource.setDriverClassName("org.postgresql.Driver");
                dataSource.setUrl(buildUrl(databaseConnection, "jdbc:postgresql"));
            }
            case MYSQL -> {
                dataSource.setDriverClassName("com.mysql.jdbc.Driver");
                dataSource.setUrl(buildUrl(databaseConnection, "jdbc:mysql"));
            }
            default -> throw new RuntimeException("Unsupported database type: " + databaseConnection.getDb());
        }

        dataSource.setUsername(databaseConnection.getUsername());
        dataSource.setPassword(databaseConnection.getPassword());

        return dataSource;
    }

    private String buildUrl(DatabaseConnection databaseConnection, String jdbcPrefix) {
        return String.format("%s://%s:%s/%s", jdbcPrefix, lifecycleConfig.getPostgresIpAddress(), lifecycleConfig.getPostgresHostPort(), databaseConnection.getDb());
    }
}
