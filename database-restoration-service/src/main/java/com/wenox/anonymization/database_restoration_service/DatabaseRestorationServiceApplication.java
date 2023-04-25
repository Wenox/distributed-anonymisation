package com.wenox.anonymization.database_restoration_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DatabaseRestorationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatabaseRestorationServiceApplication.class, args);
	}

}
