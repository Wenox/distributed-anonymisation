package com.anonymization.etl.transform.script;

import com.anonymization.etl.core.KafkaSink;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

public interface Column2ScriptService {

    Tuple2<Column2Script, AnonymizationTask> create(Tuple2<ColumnTuple, AnonymizationTask> tuple2,
                                                    Broadcast<KafkaSink> kafkaSinkBroadcast);
}