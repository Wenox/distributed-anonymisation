package com.wenox.anonymization.database_restoration_service.adapters.outbound.column_tuple;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ColumnTupleDatabaseConnectionConfig {

    private final String postgresIpAddress;
    private final String postgresHostPort;
    private final String postgresContainerPort;

    public ColumnTupleDatabaseConnectionConfig(@Value("${POSTGRES_IP_ADDRESS:postgres}") String postgresIpAddress,
                                               @Value("${POSTGRES_HOST_PORT:5432}") String postgresHostPort,
                                               @Value("${POSTGRES_CONTAINER_PORT:5432}") String postgresContainerPort) {
        this.postgresIpAddress = postgresIpAddress;
        this.postgresHostPort = postgresHostPort;
        this.postgresContainerPort = postgresContainerPort;
    }
}