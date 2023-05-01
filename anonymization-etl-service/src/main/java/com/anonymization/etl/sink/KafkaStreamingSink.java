package com.anonymization.etl.sink;

import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaStreamingSink implements StreamingSink {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaHost;

    public void sink(Dataset<String> finishedTasks) throws TimeoutException, StreamingQueryException {
        finishedTasks.selectExpr("CAST(value AS STRING) AS key", "CAST(value AS STRING) AS value")
                .writeStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", kafkaHost)
                .option("topic", KafkaConstants.TOPIC_LOAD_SUCCESS)
                .option("checkpointLocation", "<your_checkpoint_location>")
                .start()
                .awaitTermination();
    }
}
