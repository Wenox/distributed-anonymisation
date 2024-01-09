package com.anonymization.anonymization_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class AnonymizationExecutionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnonymizationExecutionServiceApplication.class, args);
    }
}
