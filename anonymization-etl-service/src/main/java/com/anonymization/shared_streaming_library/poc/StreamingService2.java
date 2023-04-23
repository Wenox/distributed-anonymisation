package com.anonymization.shared_streaming_library.poc;

import com.anonymization.shared_streaming_library.AnonymizationTask;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
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
public class StreamingService2 {

    private final SparkSession sparkSession;

    public void processAnimalTask() throws TimeoutException, StreamingQueryException {
        // Read the Kafka stream
        Dataset<AnimalTask> inputDF = sparkSession.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9093")
                .option("subscribe", KafkaConstants.TOPIC_OPERATIONS)
                .option("startingOffsets", "earliest")
                .load()
                .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
                .as(Encoders.tuple(Encoders.STRING(), Encoders.STRING()))
                .map((MapFunction<Tuple2<String, String>, AnimalTask>) tuple -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(tuple._2, AnimalTask.class);
                }, Encoders.bean(AnimalTask.class));

        // Process the input DataFrame
        Dataset<AnimalTask> processedDF = inputDF.map((MapFunction<AnimalTask, AnimalTask>) animalTask -> {
            String animalId = animalTask.getAnimalId();
            String animalName = animalTask.getAnimalName();
            Integer age = animalTask.getAge();

            // Increment the age and reverse the animal name
            Integer newAge = age + 1;
            String reversedAnimalName = new StringBuilder(animalName).reverse().toString();

            AnimalTask processedTask = new AnimalTask();
            processedTask.setAnimalId(animalId);
            processedTask.setAnimalName(reversedAnimalName);
            processedTask.setAge(newAge);

            log.info("Returning processed task: {}", processedTask);

            return processedTask;
        }, Encoders.bean(AnimalTask.class));


        processedDF.selectExpr("CAST(animalId AS STRING) AS key", "to_json(struct(*)) AS value")
                .writeStream()
                .foreachBatch((VoidFunction2<Dataset<Row>, Long>) this::processBatch)
                .option("checkpointLocation", "<your_checkpoint_location>")
                .start()
                .awaitTermination();
    }

    private void processBatch(Dataset<Row> batch, long batchId) {
        // Convert the DataFrame to a Dataset of SuccessEvent objects
        Dataset<SuccessEvent> successEvents = batch.map((MapFunction<Row, SuccessEvent>) row -> {
            SuccessEvent successEvent = new SuccessEvent();
            successEvent.setAnimalId(row.getAs("key"));
            successEvent.setCount(1); // Set the initial count to 1
            return successEvent;
        }, Encoders.bean(SuccessEvent.class));

        printDataset(successEvents, String.format("BatchId: %d, successEvents:", batchId));

        log.info("BatchId: {}, successEvent: {}", batchId, successEvents);

        // Write the Dataset of SuccessEvent objects to Kafka
        successEvents.selectExpr("CAST(animalId AS STRING) AS key", "to_json(struct(*)) AS value")
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

