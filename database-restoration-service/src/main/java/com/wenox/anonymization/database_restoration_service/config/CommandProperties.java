package com.wenox.anonymization.database_restoration_service.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ToString
@Configuration
@ConfigurationProperties(prefix = "command")
public class CommandProperties {
    private String createDatabase;
    private String dropDatabase;
    private String existsDatabase;
    private RestoreDump restoreDump;

    @Data
    public static class RestoreDump {
        private String fromArchive;
        private String fromScript;
    }
}