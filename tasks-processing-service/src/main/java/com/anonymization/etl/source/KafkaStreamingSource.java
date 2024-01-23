package com.anonymization.etl.source;

import com.anonymization.etl.domain.tasks.Task;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaStreamingSource implements StreamingSource {

    private final SparkSession sparkSession;

    public Dataset<Task> fetchTasks() {
        return sparkSession.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "kafka:9093")
                .option("subscribe", KafkaConstants.TOPIC_OPERATIONS)
                .option("startingOffsets", "earliest")
                .load()
                .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
                .as(Encoders.tuple(Encoders.STRING(), Encoders.STRING()))
                .map((MapFunction<Tuple2<String, String>, Task>) tuple -> deserializeTask(tuple._2), Encoders.bean(Task.class));
    }

    public Task deserializeTask(String value) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(value, Task.class);
        } catch (IOException e) {
            log.error("Error deserializing JSON", e);
            throw new RuntimeException("Error deserializing JSON", e);
        }
    }
}
