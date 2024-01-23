package com.wenox.anonymization.metadata_extraction_service.adapters.outbound.metadata_extraction;

import com.wenox.anonymization.metadata_extraction_service.domain.model.DatabaseConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataSourceFactory {

    // quick poc
    private static final String POSTGRES_HOST = "postgres";
    private static final String POSTGRES_PORT = "5432";

    public DataSource getDataSource(DatabaseConnection databaseConnection) {
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
        return String.format("%s://%s:%s/%s", jdbcPrefix, POSTGRES_HOST, POSTGRES_PORT, databaseConnection.getDb());
    }
}