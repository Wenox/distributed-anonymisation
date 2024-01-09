package com.anonymization.etl.transform.script;

import com.anonymization.etl.core.KafkaSink;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.io.Serializable;

public interface Column2ScriptService extends Serializable {

    Tuple2<Column2Script, AnonymizationTask> create(Tuple2<ColumnTuple, AnonymizationTask> tuple2,
                                                    Broadcast<KafkaSink> kafkaSinkBroadcast);
}
