package com.anonymization.etl.core;

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
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.springframework.context.annotation.DependsOn;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import scala.Tuple2;

import javax.annotation.PostConstruct;
import java.io.Serializable;
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
    private final BroadcastSettings broadcastSettings;

    private transient final CircuitBreakerRegistry circuitBreakerRegistry;
    private transient final RetryTemplate retryTemplate;

    private final ExtractService extractService;
    private final TransformService transformService;
    private final Column2ScriptService column2ScriptService;
    private final LoadService loadService;

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

        BroadcastFacade broadcastFacade = BroadcastFacade.create(broadcastSettings);

        CircuitBreaker circuitBreaker = getCircuitBreaker();

        Try<Void> result = Try.run(() -> {

            // Step 1: Read the Kafka stream
            Dataset<AnonymizationTask> inputDF = streamingSource.fetchTasks();

            // Repartition the input data into a specified number of partitions
            Dataset<AnonymizationTask> repartitionedInputDF = inputDF.repartition(4);

            // Step 2: Extract
            Dataset<Tuple2<ColumnTuple, AnonymizationTask>> extractedTuple = repartitionedInputDF.map(
                    (MapFunction<AnonymizationTask, Tuple2<ColumnTuple, AnonymizationTask>>) task -> extractService.extract(task, broadcastFacade),
                    Encoders.tuple(Encoders.bean(ColumnTuple.class), Encoders.bean(AnonymizationTask.class))
            );

            // Step 3: Transform - Anonymization
            Dataset<Tuple2<ColumnTuple, AnonymizationTask>> anonymizedTuple = extractedTuple.map(
                    (MapFunction<Tuple2<ColumnTuple, AnonymizationTask>, Tuple2<ColumnTuple, AnonymizationTask>>) task -> transformService.anonymize(task, broadcastFacade.getKafkaSinkBroadcast()),
                    Encoders.tuple(Encoders.bean(ColumnTuple.class), Encoders.bean(AnonymizationTask.class))
            );

            // Step 4: Transform – SQL script
            Dataset<Tuple2<Column2Script, AnonymizationTask>> scriptTuple = anonymizedTuple.map(
                    (MapFunction<Tuple2<ColumnTuple, AnonymizationTask>, Tuple2<Column2Script, AnonymizationTask>>) task -> column2ScriptService.create(task, broadcastFacade.getKafkaSinkBroadcast()),
                    Encoders.tuple(Encoders.bean(Column2Script.class), Encoders.bean(AnonymizationTask.class))
            );

            // Step 5: Load
            Dataset<SuccessEvent> successEvents = scriptTuple.map(
                    (MapFunction<Tuple2<Column2Script, AnonymizationTask>, SuccessEvent>) task -> loadService.load(task, broadcastFacade.getS3SinkBroadcast()),
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

