package com.anonymization.etl.core;

import com.anonymization.etl.config.tests.KafkaProducerFactory;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.SuccessEvent;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import com.anonymization.etl.extract.ExtractService;
import com.anonymization.etl.load.LoadService;
import com.anonymization.etl.sink.StreamingSink;
import com.anonymization.etl.source.StreamingSource;
import com.anonymization.etl.transform.script.Column2Script;
import com.anonymization.etl.transform.script.Column2ScriptService;
import com.anonymization.etl.transform.operations.TransformService;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.apache.spark.sql.*;
import org.springframework.context.annotation.DependsOn;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
@DependsOn("anonymizationTaskSimulator")
@RequiredArgsConstructor
public class AnonymizationEtlStreamingService implements EtlStreamingService, Serializable {

    private final StreamingSource streamingSource;
    private final StreamingSink streamingSink;

    private transient final CircuitBreakerRegistry circuitBreakerRegistry;
    private transient final RetryTemplate retryTemplate;

    private final ExtractService extractService;
    private final TransformService transformService;
    private final Column2ScriptService column2ScriptService;
    private final LoadService loadService;

    KafkaProducer<String, Object> kafkaProducer = KafkaProducerFactory.createProducer();


    private final Lock lock = new ReentrantLock();

    @PostConstruct
    public void init() {
        new Thread(this::startEtlStreamingQuery).start();
    }

    private void startEtlStreamingQuery() {
        retryTemplate.execute(retryContext -> {
            processEtlStreaming();
            return null;
        });
    }

    @Scheduled(fixedDelayString = "${streaming.restartInterval:60000}")
    public void checkAndRestartEtlStreamingQuery() throws InterruptedException {
        if (lock.tryLock(5, TimeUnit.SECONDS)) {
            try {
                startEtlStreamingQuery();
            } finally {
                lock.unlock();
            }
        }
    }

    @Async
    public void processEtlStreaming() {
        CircuitBreaker circuitBreaker = getCircuitBreaker();

        Try<Void> result = Try.run(() -> {

            // Step 1: Read the Kafka stream
            Dataset<AnonymizationTask> inputDF = streamingSource.fetchTasks();

            // Repartition the input data into a specified number of partitions
            Dataset<AnonymizationTask> repartitionedInputDF = inputDF.repartition(4);

            // Step 2: Extract
            Dataset<Tuple2<ColumnTuple, AnonymizationTask>> extractedTuple = inputDF.mapPartitions(
                    (MapPartitionsFunction<AnonymizationTask, Tuple2<ColumnTuple, AnonymizationTask>>) tasks -> extractService.extract(tasks),
                    Encoders.tuple(Encoders.bean(ColumnTuple.class), Encoders.bean(AnonymizationTask.class))
            );


//            Dataset<Tuple2<ColumnTuple, AnonymizationTask>> extractedTuple = repartitionedInputDF.mapPartitions(
//                    (MapPartitionsFunction<AnonymizationTask, Tuple2<ColumnTuple, AnonymizationTask>>) tasks -> extractService.extract(tasks),
//                    Encoders.tuple(Encoders.bean(ColumnTuple.class), Encoders.bean(AnonymizationTask.class))
//            );

            // Step 3: Transform - Anonymization
            Dataset<Tuple2<ColumnTuple, AnonymizationTask>> anonymizedTuple = extractedTuple.map(
                    (MapFunction<Tuple2<ColumnTuple, AnonymizationTask>, Tuple2<ColumnTuple, AnonymizationTask>>) transformService::anonymize,
                    Encoders.tuple(Encoders.bean(ColumnTuple.class), Encoders.bean(AnonymizationTask.class))
            );

            // Step 4: Transform – SQL script
            Dataset<Tuple2<Column2Script, AnonymizationTask>> scriptTuple = anonymizedTuple.map(
                    (MapFunction<Tuple2<ColumnTuple, AnonymizationTask>, Tuple2<Column2Script, AnonymizationTask>>) column2ScriptService::create,
                    Encoders.tuple(Encoders.bean(Column2Script.class), Encoders.bean(AnonymizationTask.class))
            );

            // Step 5: Load
            Dataset<SuccessEvent> successEvents = scriptTuple.map(
                    (MapFunction<Tuple2<Column2Script, AnonymizationTask>, SuccessEvent>) loadService::load,
                    Encoders.bean(SuccessEvent.class)
            );

            // Step 6: Sink
            streamingSink.sink(successEvents);

        }).recover(throwable -> {
            log.error("Error occurred during ETL processing", throwable);
            return null;
        });

        circuitBreaker.executeSupplier(() -> result);
    }

    private CircuitBreaker getCircuitBreaker() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("etlStreamingCircuitBreaker");
        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> log.info("Circuit breaker state transition: {}", event));
        return circuitBreaker;
    }
}

