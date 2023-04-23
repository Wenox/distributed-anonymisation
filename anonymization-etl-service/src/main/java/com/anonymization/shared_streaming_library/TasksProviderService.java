package com.anonymization.shared_streaming_library;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;
import scala.runtime.AbstractFunction1;

import java.io.IOException;

@Slf4j
@Service
public class TasksProviderService {

    public Dataset<AnonymizationTask> retrieveTasks(SparkSession spark, String topic) {
        Dataset<Row> df = readFromTopic(spark, topic);

        Dataset<AnonymizationTask> events = df.selectExpr("CAST(value AS STRING) AS value")
                .as(Encoders.STRING())
                .map(new AbstractFunction1<>() {
                    @Override
                    public AnonymizationTask apply(String value) {
                        return deserializeAnonymizationTask(value);
                    }
                }, Encoders.bean(AnonymizationTask.class));

        return events;
    }

    private Dataset<Row> readFromTopic(SparkSession spark, String topic) {
        return spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9092")
                .option("subscribe", topic)
                .option("startingOffsets", "earliest")
                .load();
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
