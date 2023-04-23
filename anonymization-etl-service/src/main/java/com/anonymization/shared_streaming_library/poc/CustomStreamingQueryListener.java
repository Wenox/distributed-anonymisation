package com.anonymization.shared_streaming_library.poc;

import org.apache.spark.sql.streaming.StreamingQueryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomStreamingQueryListener extends StreamingQueryListener {

    private static final Logger logger = LoggerFactory.getLogger(CustomStreamingQueryListener.class);

    @Override
    public void onQueryStarted(QueryStartedEvent event) {
        // You can implement custom logic when the query starts.
        logger.info("Query started");
    }

    @Override
    public void onQueryProgress(QueryProgressEvent event) {
        long numRows = event.progress().numInputRows();
        if (numRows == 0) {
            logger.info("No new messages");
        } else {
            logger.info("New messages");
        }
    }

    @Override
    public void onQueryTerminated(QueryTerminatedEvent event) {
        // You can implement custom logic when the query terminates.
        logger.info("on query terminated");
    }
}

