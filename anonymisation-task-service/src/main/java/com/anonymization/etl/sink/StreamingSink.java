package com.anonymization.etl.sink;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.streaming.StreamingQueryException;

import java.io.Serializable;
import java.util.concurrent.TimeoutException;

public interface StreamingSink extends Serializable {

    void sink(Dataset<String> finishedTasks) throws TimeoutException, StreamingQueryException;
}
