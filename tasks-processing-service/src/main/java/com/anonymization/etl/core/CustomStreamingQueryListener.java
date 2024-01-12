package com.anonymization.etl.core;

import org.apache.spark.sql.streaming.StreamingQueryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomStreamingQueryListener extends StreamingQueryListener {

    private static final Logger logger = LoggerFactory.getLogger(CustomStreamingQueryListener.class);

    @Override
    public void onQueryStarted(QueryStartedEvent event) {
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
        logger.info("on query terminated");
    }
}

