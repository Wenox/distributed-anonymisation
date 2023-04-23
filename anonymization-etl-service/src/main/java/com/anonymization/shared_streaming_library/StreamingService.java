package com.anonymization.shared_streaming_library;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import scala.Tuple2;
import scala.runtime.AbstractFunction1;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public abstract class StreamingService {

    private final ExtractService extractService;
    private final TransformService transformService;
    private final Column2ScriptService column2ScriptService;
    private final LoadService loadService;

    private final SparkSession spark;
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
            process();
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

    public void process() {
        Dataset<Row> df = readFromSource();

        Dataset<AnonymizationTask> events = df.selectExpr("CAST(value AS STRING) AS value")
                .as(Encoders.STRING())
                .map(new AbstractFunction1<>() {
                    @Override
                    public AnonymizationTask apply(String value) {
                        return deserializeAnonymizationTask(value);
                    }
                }, Encoders.bean(AnonymizationTask.class));

        // Extract step
        Dataset<Tuple2<Column2, AnonymizationTask>> extractedTuple = events.map(
                (MapFunction<AnonymizationTask, Tuple2<Column2, AnonymizationTask>>) extractService::extract,
                Encoders.tuple(Encoders.bean(Column2.class), Encoders.bean(AnonymizationTask.class))
        );

        // Transform - step 1 - anonymization
        Dataset<Tuple2<Column2, AnonymizationTask>> anonymizedTuple = transformService.anonymize(extractedTuple);

        // Transform – step 2 – SQL script
        Dataset<Tuple2<Column2Script, AnonymizationTask>> scriptTuple = anonymizedTuple.map(
                (MapFunction<Tuple2<Column2, AnonymizationTask>, Tuple2<Column2Script, AnonymizationTask>>) column2ScriptService::create,
                Encoders.tuple(Encoders.bean(Column2Script.class), Encoders.bean(AnonymizationTask.class))
        );

        // Load step
        Dataset<SuccessEvent> successEvents = scriptTuple.map(
                (MapFunction<Tuple2<Column2Script, AnonymizationTask>, SuccessEvent>) loadService::load,
                Encoders.bean(SuccessEvent.class)
        );

        try {
            writeToSink(successEvents);
        } catch (Exception ex) {
            log.error("Exception occurred during processing.", ex);
            ex.printStackTrace();
        }
    }

    private Dataset<Row> readFromSource() {
        return spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9092")
                .option("subscribe", "suppression_topic")
                .option("startingOffsets", "earliest")
                .load();
    }

    private StreamingQuery writeToSink(Dataset<SuccessEvent> events) throws TimeoutException {
        return events.writeStream()
                .format("kafka")
                .outputMode("update")
                .option("kafka.bootstrap.servers", "localhost:9092")
                .option("topic", "success_topic")
                .start();
    }

    public AnonymizationTask deserializeAnonymizationTask(String value) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;

        try {
            rootNode = objectMapper.readTree(value);
        } catch (IOException e) {
            log.error("Error reading JSON", e);
            throw new RuntimeException("Error reading JSON", e);
        }

        OperationType type = OperationType.valueOf(rootNode.get("type").asText().toUpperCase());

        switch (type) {
            case SUPPRESSION:
                return deserializeJson(value, SuppressionTask.class);
            default:
                throw new RuntimeException("Unknown task type: " + type);
        }
    }

    public <T> T deserializeJson(String value, Class<T> targetClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(value, targetClass);
        } catch (IOException e) {
            log.error("Error deserializing JSON", e);
            throw new RuntimeException("Error deserializing JSON", e);
        }
    }
}
