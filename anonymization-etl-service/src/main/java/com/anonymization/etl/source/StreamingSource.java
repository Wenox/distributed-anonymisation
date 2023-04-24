package com.anonymization.etl.source;

import com.anonymization.etl.AnonymizationTask;
import org.apache.spark.sql.Dataset;

// todo make generic and extract mapping jackson function from implementation
public interface StreamingSource {

    Dataset<AnonymizationTask> fetchTasks();
}