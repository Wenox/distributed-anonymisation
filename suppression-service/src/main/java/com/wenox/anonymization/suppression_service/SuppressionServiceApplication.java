package com.wenox.anonymization.suppression_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SuppressionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuppressionServiceApplication.class, args);
    }
}
