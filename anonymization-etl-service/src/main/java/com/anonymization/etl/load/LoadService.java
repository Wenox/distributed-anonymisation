package com.anonymization.etl.load;

import com.anonymization.etl.core.S3Sink;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import com.anonymization.etl.transform.script.Column2Script;
import com.anonymization.etl.domain.SuccessEvent;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;
import java.io.Serializable;


public interface LoadService extends Serializable {

    SuccessEvent load(Tuple2<Column2Script, AnonymizationTask> scriptTuple,
                      Broadcast<S3Sink> s3SinkBroadcast);
}
