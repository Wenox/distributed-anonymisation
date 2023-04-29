package com.anonymization.etl.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

@Slf4j
public class KafkaSink implements Serializable {
    private final Supplier<KafkaProducer<String, Object>> producerSupplier;
    private transient KafkaProducer<String, Object> producer;

    public KafkaSink(Supplier<KafkaProducer<String, Object>> producerSupplier) {
        this.producerSupplier = producerSupplier;
    }

    private KafkaProducer<String, Object> getProducer() {
        log.info("CALLING GET-PRODUCER");
        if (producer == null) {
        log.info("PRODUCER == NULL @@@@@@@@");
            producer = producerSupplier.get();
        }
        return producer;
    }

    public void send(String topic, Object value) {
        getProducer().send(new ProducerRecord<>(topic, value));
    }

    public static KafkaSink apply() {
        Supplier<KafkaProducer<String, Object>> supplier = new KafkaProducerSupplier();
        return new KafkaSink(supplier);
    }
}
