package com.anonymization.etl;

import scala.Tuple2;

public interface TransformService {

    Tuple2<Column2, AnonymizationTask> anonymize(Tuple2<Column2, AnonymizationTask> input);
}
