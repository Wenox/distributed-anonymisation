package com.anonymization.shared_streaming_library.sink;

import com.anonymization.shared_streaming_library.poc.SuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchStreamingSink implements StreamingSink, Serializable {

    private final KafkaBatchProcess kafkaBatchProcess;

    public void sink(Dataset<SuccessEvent> successEvents) throws TimeoutException, StreamingQueryException {
        successEvents.selectExpr("CAST(taskId AS STRING) AS key", "to_json(struct(*)) AS value")
                .writeStream()
                .foreachBatch((VoidFunction2<Dataset<Row>, Long>) kafkaBatchProcess::processBatch)
                .option("checkpointLocation", "<your_checkpoint_location>")
                .start()
                .awaitTermination();
    }
}
