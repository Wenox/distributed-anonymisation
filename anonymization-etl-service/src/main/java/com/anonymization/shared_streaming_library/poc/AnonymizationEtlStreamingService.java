package com.anonymization.shared_streaming_library.poc;

import com.anonymization.shared_streaming_library.*;
import com.anonymization.shared_streaming_library.sink.StreamingSink;
import com.anonymization.shared_streaming_library.source.StreamingSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.springframework.context.annotation.DependsOn;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
@DependsOn("anonymizationTaskSimulator")
public class AnonymizationEtlStreamingService implements EtlStreamingService, Serializable {

    private final StreamingSource streamingSource;
    private final StreamingSink streamingSink;
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
        try {
            retryTemplate.execute(retryContext -> {
                processEtlStreaming();
                return null;
            });
        } catch (Exception ex) {
            log.error("Error occurred during ETL processing", ex);
        }
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

    public void processEtlStreaming() throws TimeoutException, StreamingQueryException {
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
    }
}

