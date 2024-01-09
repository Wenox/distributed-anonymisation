package com.anonymization.etl.core;

import lombok.Builder;
import lombok.Data;
import org.apache.spark.sql.SparkSession;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Data
@Builder
public class BroadcastSettings implements Serializable {
    private final SparkSession sparkSession;
    private final String redisUrl;
    private final String kafkaHost;
    private final String awsAccessKeyId;
    private final String awsSecretAccessKey;
    private final String awsRegion;
    private final String restorationServiceUrl;

    // necessary workaround around Spark serializability and broadcast issues
    @Configuration
    public static class BroadcastSpringConfiguration {

        @Value("${redis.url}")
        private String redisUrl;

        @Value("${spring.kafka.bootstrap-servers}")
        private String kafkaHost;

        @Value("${aws.access_key_id}")
        private String awsAccessKeyId;

        @Value("${aws.secret_access_key}")
        private String awsSecretAccessKey;

        @Value("${aws.region}")
        private String awsRegion;

        @Value("${restoration-service.url}")
        private String restorationServiceUrl;

        @Bean
        public BroadcastSettings broadcastConfig(SparkSession sparkSession) {
            return BroadcastSettings.builder()
                    .sparkSession(sparkSession)
                    .redisUrl(redisUrl)
                    .kafkaHost(kafkaHost)
                    .awsAccessKeyId(awsAccessKeyId)
                    .awsSecretAccessKey(awsSecretAccessKey)
                    .awsRegion(awsRegion)
                    .restorationServiceUrl(restorationServiceUrl)
                    .build();
        }
    }
}
