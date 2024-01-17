package com.anonymization.etl.source;

import com.anonymization.etl.domain.tasks.Task;
import org.apache.spark.sql.Dataset;

import java.io.Serializable;

// todo make generic and extract mapping jackson function from implementation
public interface StreamingSource extends Serializable {

    Dataset<Task> fetchTasks();
}