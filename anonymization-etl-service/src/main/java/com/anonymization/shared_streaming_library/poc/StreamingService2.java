package com.anonymization.shared_streaming_library.poc;

import com.anonymization.shared_streaming_library.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamingService2 {

    private final SparkSession sparkSession;
    private final ExtractService extractService;
    private final TransformService transformService;
    private final Column2ScriptService column2ScriptService;
    private final LoadService loadService;

    public void processAnonymizationTasks() throws TimeoutException, StreamingQueryException {
        // Read the Kafka stream
        Dataset<AnonymizationTask> inputDF = sparkSession.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9093")
                .option("subscribe", KafkaConstants.TOPIC_OPERATIONS)
                .option("startingOffsets", "earliest")
                .load()
                .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
                .as(Encoders.tuple(Encoders.STRING(), Encoders.STRING()))
                .map((MapFunction<Tuple2<String, String>, AnonymizationTask>) tuple -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(tuple._2, AnonymizationTask.class);
                }, Encoders.bean(AnonymizationTask.class));

        // Extract step
        Dataset<Tuple2<Column2, AnonymizationTask>> extractedTuple = inputDF.map(
                (MapFunction<AnonymizationTask, Tuple2<Column2, AnonymizationTask>>) extractService::extract,
                Encoders.tuple(Encoders.bean(Column2.class), Encoders.bean(AnonymizationTask.class))
        );

        // Transform - step 1 - anonymization
        Dataset<Tuple2<Column2, AnonymizationTask>> anonymizedTuple = extractedTuple.map(
                (MapFunction<Tuple2<Column2, AnonymizationTask>, Tuple2<Column2, AnonymizationTask>>) transformService::anonymize,
                Encoders.tuple(Encoders.bean(Column2.class), Encoders.bean(AnonymizationTask.class))
        );

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

