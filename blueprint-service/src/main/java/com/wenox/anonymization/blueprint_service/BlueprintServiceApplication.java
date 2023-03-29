package com.wenox.anonymization.blueprint_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@SpringBootApplication
public class BlueprintServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlueprintServiceApplication.class, args);
	}

}
