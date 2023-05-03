package com.anonymization.mirror_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@SpringBootApplication
public class MirrorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MirrorServiceApplication.class, args);
    }

}
