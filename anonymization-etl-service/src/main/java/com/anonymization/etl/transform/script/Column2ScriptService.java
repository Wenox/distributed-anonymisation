package com.anonymization.etl.transform.script;

import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import scala.Tuple2;

public interface Column2ScriptService {

    Tuple2<Column2Script, AnonymizationTask> create(Tuple2<ColumnTuple, AnonymizationTask> tuple2);
}
