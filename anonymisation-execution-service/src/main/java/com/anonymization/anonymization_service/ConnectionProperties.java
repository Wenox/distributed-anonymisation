package com.anonymization.anonymization_service;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ToString
@Configuration
@ConfigurationProperties(prefix = "mirror.db")
public class ConnectionProperties {
    private String host;
    private String port;
    private String username;
}
