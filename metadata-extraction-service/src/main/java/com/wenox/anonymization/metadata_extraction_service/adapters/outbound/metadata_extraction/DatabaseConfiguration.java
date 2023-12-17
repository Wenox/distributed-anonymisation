package com.wenox.anonymization.metadata_extraction_service.adapters.outbound.metadata_extraction;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class DatabaseConfiguration {

    private final Boolean isRunningOnCloud;
    private final String postgresIpAddress;
    private final String postgresHostPort;
    private final String postgresContainerPort;

    public DatabaseConfiguration(@Value("${server.environment.cloud}") Boolean isRunningOnCloud,
                                 @Value("${POSTGRES_IP_ADDRESS:localhost}") String postgresIpAddress,
                                 @Value("${POSTGRES_HOST_PORT:5432}") String postgresHostPort,
                                 @Value("${POSTGRES_CONTAINER_PORT:5432}") String postgresContainerPort) {
        this.isRunningOnCloud = isRunningOnCloud;
        this.postgresIpAddress = postgresIpAddress;
        this.postgresHostPort = postgresHostPort;
        this.postgresContainerPort = postgresContainerPort;
    }
}