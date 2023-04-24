package com.anonymization.shared_streaming_library;

import com.anonymization.shared_streaming_library.poc.tasks.ShuffleTask;
import com.anonymization.shared_streaming_library.poc.tasks.SuppressionTask;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.io.IOException;
import java.io.Serializable;

@Slf4j
@Service
@RequiredArgsConstructor
public class TasksProviderService implements Serializable {

    private final SparkSession sparkSession;

    public Dataset<AnonymizationTask> fetchTasks() {
        return sparkSession.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9093")
                .option("subscribe", KafkaConstants.TOPIC_OPERATIONS)
                .option("startingOffsets", "earliest")
                .load()
                .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
                .as(Encoders.tuple(Encoders.STRING(), Encoders.STRING()))
                .map((MapFunction<Tuple2<String, String>, AnonymizationTask>) tuple -> deserializeAnonymizationTask(tuple._2), Encoders.bean(AnonymizationTask.class));
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
                log.info("Deserializing into suppression... {}", value);
                return deserializeJson(value, SuppressionTask.class);
            case SHUFFLE:
                log.info("Deserializing into shuffle... {}", value);
                return deserializeJson(value, ShuffleTask.class);
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
