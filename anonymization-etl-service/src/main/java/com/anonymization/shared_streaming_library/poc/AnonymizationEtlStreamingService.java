package com.anonymization.shared_streaming_library.poc;

import com.anonymization.shared_streaming_library.*;
import com.anonymization.shared_streaming_library.sink.StreamingSink;
import com.anonymization.shared_streaming_library.source.StreamingSource;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.springframework.context.annotation.DependsOn;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
@DependsOn("anonymizationTaskSimulator")
public class AnonymizationEtlStreamingService implements EtlStreamingService, Serializable {

    private final StreamingSource streamingSource;
    private final StreamingSink streamingSink;

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryTemplate retryTemplate;

    private final ExtractService extractService;
    private final TransformService transformService;
    private final Column2ScriptService column2ScriptService;
    private final LoadService loadService;

    private final Lock lock = new ReentrantLock();

    @PostConstruct
    public void init() {
        startEtlStreamingQuery();
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

    public void processEtlStreaming() {
        CircuitBreaker circuitBreaker = getCircuitBreaker();

        Try<Void> result = Try.run(() -> {

            // Step 1: Read the Kafka stream
            Dataset<AnonymizationTask> inputDF = streamingSource.fetchTasks();

            // Step 2: Extract
            Dataset<Tuple2<Column2, AnonymizationTask>> extractedTuple = inputDF.map(
                    (MapFunction<AnonymizationTask, Tuple2<Column2, AnonymizationTask>>) extractService::extract,
                    Encoders.tuple(Encoders.bean(Column2.class), Encoders.bean(AnonymizationTask.class))
            );

            // Step 3: Transform - Anonymization
            Dataset<Tuple2<Column2, AnonymizationTask>> anonymizedTuple = extractedTuple.map(
                    (MapFunction<Tuple2<Column2, AnonymizationTask>, Tuple2<Column2, AnonymizationTask>>) transformService::anonymize,
                    Encoders.tuple(Encoders.bean(Column2.class), Encoders.bean(AnonymizationTask.class))
            );

            // Step 4: Transform – SQL script
            Dataset<Tuple2<Column2Script, AnonymizationTask>> scriptTuple = anonymizedTuple.map(
                    (MapFunction<Tuple2<Column2, AnonymizationTask>, Tuple2<Column2Script, AnonymizationTask>>) column2ScriptService::create,
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

