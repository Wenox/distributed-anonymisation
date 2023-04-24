package com.anonymization.shared_streaming_library.poc;

import com.anonymization.shared_streaming_library.*;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
@DependsOn("anonymizationTaskSimulator")
public class AnonymizationEtlStreamingService implements Serializable {

    private final SparkSession sparkSession;
    private final TasksProviderService tasksProviderService;
    private final ExtractService extractService;
    private final TransformService transformService;
    private final Column2ScriptService column2ScriptService;
    private final LoadService loadService;

    private StreamingQuery streamingQuery;
    private final Lock lock = new ReentrantLock();

    @PostConstruct
    public void init() {
        log.info("Inside init - postconstruct");
        startStreamingQuery();
    }

    private void startStreamingQuery() {
        log.info("Inside startStreamingQuery");
        if (streamingQuery == null || streamingQuery.exception().isDefined()) {
            log.info("Calling process");
            try {
                processAnonymizationTasks();
            } catch (Exception ex) {
                log.error("Error occurred during processing", ex);
            }
        }
    }

    @Scheduled(fixedDelayString = "${streaming.restartInterval:60000}")
    public void checkAndRestartStreamingQuery() {
        log.info("Called checkAndRestart");
        log.info("Lock lock");
        lock.lock();
        try {
            log.info("Calling startStreamingQuery");
            startStreamingQuery();
        } finally {
            log.info("Lock unlock");
            lock.unlock();
        }
    }

    public void processAnonymizationTasks() throws TimeoutException, StreamingQueryException {
        // Step 0: Read the Kafka stream
        Dataset<AnonymizationTask> inputDF = tasksProviderService.fetchTasks();

        // Step 1: Extract
        Dataset<Tuple2<Column2, AnonymizationTask>> extractedTuple = inputDF.map(
                (MapFunction<AnonymizationTask, Tuple2<Column2, AnonymizationTask>>) extractService::extract,
                Encoders.tuple(Encoders.bean(Column2.class), Encoders.bean(AnonymizationTask.class))
        );

        // Step 2: Transform - anonymization
        Dataset<Tuple2<Column2, AnonymizationTask>> anonymizedTuple = extractedTuple.map(
                (MapFunction<Tuple2<Column2, AnonymizationTask>, Tuple2<Column2, AnonymizationTask>>) transformService::anonymize,
                Encoders.tuple(Encoders.bean(Column2.class), Encoders.bean(AnonymizationTask.class))
        );

        // Step 3: Transform – SQL script
        Dataset<Tuple2<Column2Script, AnonymizationTask>> scriptTuple = anonymizedTuple.map(
                (MapFunction<Tuple2<Column2, AnonymizationTask>, Tuple2<Column2Script, AnonymizationTask>>) column2ScriptService::create,
                Encoders.tuple(Encoders.bean(Column2Script.class), Encoders.bean(AnonymizationTask.class))
        );

        // Step 4: Load
        Dataset<SuccessEvent> successEvents = scriptTuple.map(
                (MapFunction<Tuple2<Column2Script, AnonymizationTask>, SuccessEvent>) loadService::load,
                Encoders.bean(SuccessEvent.class)
        );

        // Step 5: Sink
        successEvents.selectExpr("CAST(taskId AS STRING) AS key", "to_json(struct(*)) AS value")
                .writeStream()
                .foreachBatch((VoidFunction2<Dataset<Row>, Long>) this::processBatch)
                .option("checkpointLocation", "<your_checkpoint_location>")
                .start()
                .awaitTermination();
    }

    private void processBatch(Dataset<Row> batch, long batchId) {
        Dataset<SuccessEvent> successEvents = batch.map((MapFunction<Row, SuccessEvent>) row -> {
            SuccessEvent successEvent = new SuccessEvent();
            successEvent.setTaskId(row.getAs("key"));
            successEvent.setCount(1); // Set the initial count to 1
            return successEvent;
        }, Encoders.bean(SuccessEvent.class));

        printDataset(successEvents, String.format("BatchId: %d, successEvents:", batchId));

        log.info("BatchId: {}, successEvent: {}", batchId, successEvents);

        // Write the Dataset of SuccessEvent objects to Kafka
        successEvents.selectExpr("CAST(taskId AS STRING) AS key", "to_json(struct(*)) AS value")
                .write()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9093")
                .option("topic", KafkaConstants.TOPIC_OPERATION_SUCCESS)
                .save();
    }

    private void printDataset(Dataset<?> dataset, String title) {
        System.out.println(title);
        dataset.show(false);
    }
}

