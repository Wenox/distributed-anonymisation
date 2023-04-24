package com.anonymization.shared_streaming_library.sink;

import com.anonymization.shared_streaming_library.poc.SuccessEvent;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.streaming.StreamingQueryException;

import java.util.concurrent.TimeoutException;

public interface StreamingSink {

    void sink(Dataset<SuccessEvent> successEvents) throws TimeoutException, StreamingQueryException;
}
