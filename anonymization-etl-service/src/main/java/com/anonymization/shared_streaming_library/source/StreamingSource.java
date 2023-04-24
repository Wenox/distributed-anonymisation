package com.anonymization.shared_streaming_library.source;

import com.anonymization.shared_streaming_library.AnonymizationTask;
import org.apache.spark.sql.Dataset;

// todo make generic and extract mapping jackson function from implementation
public interface StreamingSource {

    Dataset<AnonymizationTask> fetchTasks();
}