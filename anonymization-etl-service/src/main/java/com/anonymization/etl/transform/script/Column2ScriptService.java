package com.anonymization.etl.transform.script;

import com.anonymization.etl.domain.tasks.AnonymizationTask;
import com.anonymization.etl.domain.Column2;
import scala.Tuple2;

public interface Column2ScriptService {

    Tuple2<Column2Script, AnonymizationTask> create(Tuple2<Column2, AnonymizationTask> tuple2);
}
