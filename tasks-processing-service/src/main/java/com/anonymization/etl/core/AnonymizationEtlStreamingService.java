package com.anonymization.etl.core;

import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.Task;
import com.anonymization.etl.extract.ExtractService;
import com.anonymization.etl.load.LoadService;
import com.anonymization.etl.sink.StreamingSink;
import com.anonymization.etl.source.StreamingSource;
import com.anonymization.etl.transform.script.Column2Script;
import com.anonymization.etl.transform.script.Column2ScriptService;
import com.anonymization.etl.transform.operations.TransformService;
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

    private transient final RetryTemplate retryTemplate;

    private final ExtractService extractService;
    private final TransformService transformService;
    private final Column2ScriptService column2ScriptService;
    private final LoadService loadService;

    private final Lock lock = new ReentrantLock();
    private final static int LOCK_TIME = 5;

    @PostConstruct
    public void init() {
        new Thread(this::startEtlPipelineStreaming).start();
    }

    private void startEtlPipelineStreaming() {
        retryTemplate.execute(retryContext -> {
            processEtlStreaming();
            return null;
        });
    }

    @Scheduled(fixedDelayString = "${task-processing.etl-pipeline.restart-interval:60000}")
    @Override
    public void checkAndRestartEtlStreamingQuery() throws InterruptedException {
        if (lock.tryLock(LOCK_TIME, TimeUnit.SECONDS)) {
            try {
                startEtlPipelineStreaming();
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * ETL processing logic.
     *
     * Unit of work: anonymization task, which describes anonymization of one specific
     * column in a table, for one specific strategy.
     *
     * For example: anonymization of column "salary" with two strategies "Generalisation"
     * and "Column Shuffle" would require two anonymization tasks.
     * Effectively two partial SQL scripts will be created in Step 5.
     *
     * Processing of anonymization task is fully idempotent and ordering-agnostic.
     *
     * Step 1: Reading of Anonymization tasks from Kafka operations topic.
     *         --> For example: { type: GENERALISATION, table: employees, column: salary, ... }
     *
     * Step 2: Extract tuple: List: of PKs and Values (e.g. employees.id and employees.salary).
     *         --> For example: { pks: [1, 2, 3], values: [30000, 28000, 42000] }
     *
     *         Extracted tuple comes from Redis if already exists there, or
     *         otherwise is fetched from restoration-service using synchronous REST call.
     *
     * Step 3: Transform: anonymize List of Values (e.g. salary) using given anonymization strategy.
     *         --> For example: { values: [ 25000-30000, 25000-30000, 40000-45000 ] }
     *
     * Step 4: Transform: anonymized List is transformed into the partial SQL script for this column.
     *         Partial SQL script contains UPDATE and ALTER column type queries.
     *         --> For example:   ALTER TABLE employees ALTER COLUMN salary TYPE TEXT USING '';
     *                            UPDATE employees SET salary = '25000-30000' WHERE id = 1;
     *                            UPDATE employees SET salary = '25000-30000' WHERE id = 2;
     *                            UPDATE employees SET salary = '40000-45000' WHERE id = 3; }
     *
     * Step 5: Load: the partial SQL script is loaded into S3.
     *
     * Step 6: Sink success into Kafka.
     *
     * Each step publishes Kafka message for external observability.
     *
     * ...Further processing is performed by other services (e.g., execution of SQL scripts against Restoration Database)
     * ...For example by anonymisation-orchestration-service.
     * */
    @Async
    @Override
    public void processEtlStreaming() {

        BroadcastFacade broadcastFacade = BroadcastFacade.create(broadcastSettings);

        Try<Void> result = Try.run(() -> {

            // Step 1: Read from Kafka
            Dataset<Task> partitionedDataset = streamingSource.fetchTasks().repartition(2);

            // Step 2: Extract
            Dataset<Tuple2<ColumnTuple, Task>> extractedTuple = partitionedDataset.map(
                    (MapFunction<Task, Tuple2<ColumnTuple, Task>>) task -> extractService.extract(task, broadcastFacade),
                    Encoders.tuple(Encoders.bean(ColumnTuple.class), Encoders.bean(Task.class))
            );

            // Step 3: Transform - Anonymization
            Dataset<Tuple2<ColumnTuple, Task>> anonymizedTuple = extractedTuple.map(
                    (MapFunction<Tuple2<ColumnTuple, Task>, Tuple2<ColumnTuple, Task>>) task -> transformService.anonymize(task, broadcastFacade.getKafkaSinkBroadcast()),
                    Encoders.tuple(Encoders.bean(ColumnTuple.class), Encoders.bean(Task.class))
            );

            // Step 4: Transform – SQL script
            Dataset<Tuple2<Column2Script, Task>> scriptTuple = anonymizedTuple.map(
                    (MapFunction<Tuple2<ColumnTuple, Task>, Tuple2<Column2Script, Task>>) task -> column2ScriptService.create(task, broadcastFacade.getKafkaSinkBroadcast()),
                    Encoders.tuple(Encoders.bean(Column2Script.class), Encoders.bean(Task.class))
            );

            // Step 5: Load fragments into S3
            Dataset<String> finishedTasks = scriptTuple.map(
                    (MapFunction<Tuple2<Column2Script, Task>, String>) task -> loadService.load(task, broadcastFacade.getS3SinkBroadcast()),
                    Encoders.bean(String.class)
            );

            // Step 6: Kafka sink
            streamingSink.sink(finishedTasks);

        }).recover(throwable -> {
            log.error("Error occurred during ETL processing", throwable);
            return null;
        });
    }
}

