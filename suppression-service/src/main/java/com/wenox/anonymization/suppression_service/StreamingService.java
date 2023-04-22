package com.wenox.anonymization.suppression_service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import scala.runtime.AbstractFunction1;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamingService {

    private final SparkSession spark;
    private final ExtractService extractService;
    private final TransformService transformService;
    private final LoadService loadService;

    private StreamingQuery streamingQuery;
    private final Lock lock = new ReentrantLock();


    @PostConstruct
    public void init() {
        startStreamingQuery();
    }

    private void startStreamingQuery() {
        lock.lock();
        try {
            if (streamingQuery == null || streamingQuery.exception().isDefined()) {
                processStreamingDataFromKafka();
            }
        } finally {
            lock.unlock();
        }
    }

    @Scheduled(fixedDelayString = "${streaming.restartInterval:60000}")
    public void checkAndRestartStreamingQuery() {
        startStreamingQuery();
    }

    public void processStreamingDataFromKafka() {
        lock.lock();
        try {
            Dataset<Row> df = readFromSource();

            Dataset<SuppressionTask> events = df.selectExpr("CAST(value AS STRING) AS value")
                    .as(Encoders.STRING())
                    .map(new AbstractFunction1<>() {
                        @Override
                        public SuppressionTask apply(String value) {
                            return deserializeJson(value);
                        }
                    }, Encoders.bean(SuppressionTask.class));

            // Extract step
            Dataset<Column2> extractedColumn2 = events.map(
                    (MapFunction<SuppressionTask, Column2>) extractService::extract,
                    Encoders.bean(Column2.class)
            );

            // Transform – step 1 – anonymization
            Dataset<Column2> anonymizedColumn2 = extractedColumn2.map(
                    (MapFunction<Column2, Column2>) transformService::anonymize,
                    Encoders.bean(Column2.class)
            );

            // Transform – step 2 – SQL script
            Dataset<Column2Script> column2Scripts = anonymizedColumn2.map(
                    (MapFunction<Column2, Column2Script>) transformService::createColumn2Script,
                    Encoders.bean(Column2Script.class)
            );

            // Load step
            Dataset<SuccessEvent> successEvents = column2Scripts.map(
                    (MapFunction<Column2Script, SuccessEvent>) loadService::load,
                    Encoders.bean(SuccessEvent.class)
            );

            streamingQuery = writeToSink(successEvents);

        } catch (Exception ex) {
            log.error("Exception occurred during processing.", ex);
            ex.printStackTrace();
        } finally {
            lock.unlock();
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

    private SuppressionTask deserializeJson(String value) {
        return null;
    }
}
