package com.wenox.anonymization.metadata_extraction_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MetadataExtractionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetadataExtractionServiceApplication.class, args);
	}

}
