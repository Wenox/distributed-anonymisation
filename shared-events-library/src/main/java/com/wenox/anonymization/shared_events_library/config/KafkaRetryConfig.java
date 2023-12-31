package com.wenox.anonymization.shared_events_library.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
@EnableConfigurationProperties(KafkaRetryConfig.KafkaConsumerBackoffProperties.class)
public class KafkaRetryConfig {

    private final KafkaConsumerBackoffProperties backoffProperties;

    public KafkaRetryConfig(KafkaConsumerBackoffProperties backoffProperties) {
        this.backoffProperties = backoffProperties;
    }

    @Bean
    BackOff backoff() {
        if (!backoffProperties.isEnabled()) {
            log.info("Retrying in Kafka Consumer is disabled, will not use backoff...");
            return new FixedBackOff(0L, 0L);
        }

        return switch (backoffProperties.getType()) {
            case FIXED ->
                    new FixedBackOff(backoffProperties.getFixed().getInterval(), backoffProperties.getFixed().getMaxAttempts());
            case EXPONENTIAL ->
                    new ExponentialBackOff(backoffProperties.getExponential().getInitialInterval(), backoffProperties.getExponential().getMultiplier());
        };
    }

    @Data
    @ConfigurationProperties(prefix = "kafka.consumer.backoff")
    public static class KafkaConsumerBackoffProperties {
        private boolean enabled = true;
        private BackoffType type = BackoffType.FIXED;
        private Fixed fixed = new Fixed();
        private Exponential exponential = new Exponential();

        public enum BackoffType {
            FIXED, EXPONENTIAL
        }

        @Data
        public static class Fixed {
            private long interval = 1000L;
            private long maxAttempts = 5L;
        }

        @Data
        public static class Exponential {
            private long initialInterval = 2L;
            private double multiplier = 2.0;
        }
    }
}
