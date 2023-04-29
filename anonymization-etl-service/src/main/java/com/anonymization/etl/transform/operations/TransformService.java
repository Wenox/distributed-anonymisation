package com.anonymization.etl.transform.operations;

import com.anonymization.etl.core.KafkaSink;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

public interface TransformService {

    Tuple2<ColumnTuple, AnonymizationTask> anonymize(Tuple2<ColumnTuple, AnonymizationTask> input,
                                                     Broadcast<KafkaSink> kafkaSinkBroadcast);
}
