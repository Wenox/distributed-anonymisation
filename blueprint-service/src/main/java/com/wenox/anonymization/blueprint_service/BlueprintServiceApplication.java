package com.wenox.anonymization.blueprint_service;

import com.wenox.anonymization.s3_file_manager.SharedConfigurationReference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@SpringBootApplication
@Import(SharedConfigurationReference.class)
public class BlueprintServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlueprintServiceApplication.class, args);
	}

}
