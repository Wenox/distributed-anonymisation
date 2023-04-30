package com.anonymization.etl.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

import java.io.Serializable;
import java.util.function.Supplier;

@Slf4j
public class KafkaSink implements Serializable {

    private final Supplier<KafkaProducer<String, Object>> producerSupplier;
    private transient KafkaProducer<String, Object> producer;

    public KafkaSink(Supplier<KafkaProducer<String, Object>> producerSupplier) {
        this.producerSupplier = producerSupplier;
    }

    private KafkaProducer<String, Object> getProducer() {
        if (producer == null) {
            log.info("Preparing for KafkaSink instantiation...");
            producer = producerSupplier.get();
        }
        return producer;
    }

    public void send(String topic, Object value) {
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
