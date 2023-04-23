package com.anonymization.shared_streaming_library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnonymizationEtlService {

    public static void main(String[] args) {
        SpringApplication.run(AnonymizationEtlService.class, args);
    }
}
