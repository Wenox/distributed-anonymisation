package com.anonymization.etl.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Slf4j
public class KafkaSink implements Serializable {

    private final Supplier<KafkaProducer<String, String>> producerSupplier;
    private final AtomicReference<KafkaProducer<String, String>> producerRef;

    public KafkaSink(Supplier<KafkaProducer<String, String>> producerSupplier) {
        this.producerSupplier = producerSupplier;
        this.producerRef = new AtomicReference<>();
    }

    private KafkaProducer<String, String> getProducer() {
        return producerRef.updateAndGet(currentProducer -> {
            if (currentProducer == null) {
                log.info("Preparing for KafkaProducer instantiation...");
                return producerSupplier.get();
            }
            return currentProducer;
        });
    }

    public void send(String topic, String value) {
        log.info("Publishing to Kafka | Topic: {} | Value: {}", topic, value);
        getProducer().send(new ProducerRecord<>(topic, value));
    }

    public static KafkaSink apply(BroadcastSettings config) {
        return new KafkaSink(new KafkaProducerSupplier(config));
    }

    public static ClassTag<KafkaSink> getClassTag() {
        return ClassTag$.MODULE$.apply(KafkaSink.class);
    }
}
