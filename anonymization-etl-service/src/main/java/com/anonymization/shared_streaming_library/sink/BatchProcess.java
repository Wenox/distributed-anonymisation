package com.anonymization.shared_streaming_library.sink;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface BatchProcess {

    void process(Dataset<Row> batch, long batchId);
}
