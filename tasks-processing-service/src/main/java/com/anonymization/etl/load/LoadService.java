package com.anonymization.etl.load;

import com.anonymization.etl.core.S3Sink;
import com.anonymization.etl.domain.tasks.Task;
import com.anonymization.etl.transform.script.Column2Script;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;
import java.io.Serializable;


public interface LoadService extends Serializable {

    String load(Tuple2<Column2Script, Task> scriptTuple,
                      Broadcast<S3Sink> s3SinkBroadcast);
}
