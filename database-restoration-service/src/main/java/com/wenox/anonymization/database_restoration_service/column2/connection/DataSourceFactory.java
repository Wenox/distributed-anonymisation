package com.wenox.anonymization.database_restoration_service.column2.connection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataSourceFactory {

    private final DatabaseConfiguration databaseConfiguration;

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
            default -> throw new RuntimeException("Unsupported database type: " + databaseConnection.getDatabaseName());
        }

        dataSource.setUsername(databaseConnection.getUsername());
        dataSource.setPassword(databaseConnection.getPassword());

        return dataSource;
    }

    private String buildUrl(DatabaseConnection databaseConnection, String jdbcPrefix) {
        String ipAddress = databaseConfiguration.getIsRunningOnCloud() ? databaseConfiguration.getPostgresIpAddress() : "localhost";
        String port = databaseConfiguration.getIsRunningOnCloud() ? databaseConfiguration.getPostgresContainerPort() : databaseConfiguration.getPostgresHostPort();
        return String.format("%s://%s:%s/%s", jdbcPrefix, ipAddress, port, databaseConnection.getDatabaseName());
    }
}
