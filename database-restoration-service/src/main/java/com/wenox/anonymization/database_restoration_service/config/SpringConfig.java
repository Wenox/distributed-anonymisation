package com.wenox.anonymization.database_restoration_service.config;

import com.wenox.anonymization.database_restoration_service.domain.ports.*;
import com.wenox.anonymization.database_restoration_service.domain.service.column_tuple.ColumnTupleService;
import com.wenox.anonymization.database_restoration_service.domain.service.column_tuple.DefaultColumnTupleService;
import com.wenox.anonymization.database_restoration_service.domain.service.messaging.BlueprintCreatedService;
import com.wenox.anonymization.database_restoration_service.domain.service.messaging.MetadataExtractedFailureService;
import com.wenox.anonymization.database_restoration_service.domain.service.mirror.DefaultMirrorService;
import com.wenox.anonymization.database_restoration_service.domain.service.mirror.MirrorService;
import com.wenox.anonymization.database_restoration_service.domain.service.restoration.DefaultRestorationService;
import com.wenox.anonymization.database_restoration_service.domain.service.restoration.RestorationService;
import com.wenox.anonymization.database_restoration_service.domain.service.restoration_lifecycle.DefaultRestorationLifecycleService;
import com.wenox.anonymization.database_restoration_service.domain.service.restoration_lifecycle.RestorationLifecycleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    BlueprintCreatedService blueprintCreatedService(RestorationLifecycleService restorationLifecycleService,
                                                    RestorationService restorationService,
                                                    MessagePublisher messagePublisher) {
        return new BlueprintCreatedService(
                restorationLifecycleService,
                restorationService,
                messagePublisher
        );
    }

    @Bean
    MetadataExtractedFailureService metadataExtractionFailureService(DropDatabasePort dropDatabasePort,
                                                                     RestorationService restorationService,
                                                                     MessagePublisher messagePublisher) {
        return new MetadataExtractedFailureService(
                dropDatabasePort,
                restorationService,
                messagePublisher
        );
    }

    @Bean
    RestorationService restorationService(RestorationRepository restorationRepository) {
        return new DefaultRestorationService(restorationRepository);
    }

    @Bean
    RestorationLifecycleService restorationLifecycleService(DropDatabasePort dropDatabasePort,
                                                            CreateDatabasePort createDatabasePort,
                                                            RestoreDatabasePort restoreDatabasePort) {
        return new DefaultRestorationLifecycleService(
                dropDatabasePort,
                createDatabasePort,
                restoreDatabasePort
        );
    }

    @Bean
    ColumnTupleService columnTupleService(RestorationService restorationService,
                                          ColumnTuplePort columnTuplePort) {
        return new DefaultColumnTupleService(
                restorationService,
                columnTuplePort
        );
    }

    @Bean
    MirrorService mirrorService(WorksheetProjectionRepository worksheetProjectionRepository,
                                RestorationLifecycleService restorationLifecycleService) {
        return new DefaultMirrorService(
                worksheetProjectionRepository,
                restorationLifecycleService
        );
    }
}
