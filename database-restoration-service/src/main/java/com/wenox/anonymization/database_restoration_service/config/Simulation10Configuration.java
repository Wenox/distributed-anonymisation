package com.wenox.anonymization.database_restoration_service.config;


import com.wenox.anonymization.database_restoration_service.domain.model.Restoration;
import com.wenox.anonymization.database_restoration_service.domain.ports.RestorationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class Simulation10Configuration {

    @Bean
    public CommandLineRunner loadData(RestorationRepository repository) {
        return args -> {
            Restoration restoration = new Restoration();
            restoration.setActive(true);
            restoration.setBlueprintId("1234");
            restoration.setRunnerIp("localhost");
            repository.save(restoration);
            log.info("Created a restoration");
        };
    }
}

