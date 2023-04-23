package com.anonymization.shared_streaming_library;

import org.apache.spark.sql.Dataset;
import scala.Tuple2;

public interface TransformService {

    Tuple2<Column2, AnonymizationTask> anonymize(Tuple2<Column2, AnonymizationTask> input);
}
