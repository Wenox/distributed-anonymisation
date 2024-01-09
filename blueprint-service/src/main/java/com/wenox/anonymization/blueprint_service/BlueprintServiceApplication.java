package com.wenox.anonymization.blueprint_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableMongoRepositories
@EnableDiscoveryClient
@SpringBootApplication
public class BlueprintServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlueprintServiceApplication.class, args);
	}

}
