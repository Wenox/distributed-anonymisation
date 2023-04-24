package com.anonymization.etl.sink;

import com.anonymization.etl.domain.SuccessEvent;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaBatchProcess implements BatchProcess, Serializable {

    public void process(Dataset<Row> batch, long batchId) {
        Dataset<SuccessEvent> successEvents = batch.map((MapFunction<Row, SuccessEvent>) row -> {
            SuccessEvent successEvent = new SuccessEvent();
            successEvent.setTaskId(row.getAs("key"));
            successEvent.setCount(1); // Set the initial count to 1
            return successEvent;
        }, Encoders.bean(SuccessEvent.class));

        log.info("BatchId: {}, successEvent: {}", batchId, successEvents);

        // Write the Dataset of SuccessEvent objects to Kafka
        successEvents.selectExpr("CAST(taskId AS STRING) AS key", "to_json(struct(*)) AS value")
                .write()
                .format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9093")
                .option("topic", KafkaConstants.TOPIC_OPERATION_SUCCESS)
                .save();
    }
}
