package com.anonymization.etl.transform.script;

import com.anonymization.etl.core.KafkaSink;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.Task;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.io.Serializable;

public interface Column2ScriptService extends Serializable {

    Tuple2<Column2Script, Task> create(Tuple2<ColumnTuple, Task> tuple2,
                                       Broadcast<KafkaSink> kafkaSinkBroadcast);
}
