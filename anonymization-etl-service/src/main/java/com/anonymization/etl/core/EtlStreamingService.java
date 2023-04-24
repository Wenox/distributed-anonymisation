package com.anonymization.etl.core;

import org.apache.spark.sql.streaming.StreamingQueryException;

import java.util.concurrent.TimeoutException;

public interface EtlStreamingService {

    void processEtlStreaming() throws TimeoutException, StreamingQueryException;

    void checkAndRestartEtlStreamingQuery() throws InterruptedException;
}
