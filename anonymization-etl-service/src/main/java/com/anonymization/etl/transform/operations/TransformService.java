package com.anonymization.etl.transform.operations;

import com.anonymization.etl.domain.tasks.AnonymizationTask;
import com.anonymization.etl.domain.Column2;
import scala.Tuple2;

public interface TransformService {

    Tuple2<Column2, AnonymizationTask> anonymize(Tuple2<Column2, AnonymizationTask> input);
}
