package com.anonymization.etl.transform.operations;

import com.anonymization.etl.core.KafkaSink;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.Task;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;
import java.io.Serializable;


public interface TransformService extends Serializable {

    Tuple2<ColumnTuple, Task> anonymize(Tuple2<ColumnTuple, Task> input,
                                        Broadcast<KafkaSink> kafkaSinkBroadcast);
}
