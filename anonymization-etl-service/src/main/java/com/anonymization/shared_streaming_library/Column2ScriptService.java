package com.anonymization.shared_streaming_library;

import scala.Tuple2;

public interface Column2ScriptService {

    Tuple2<Column2Script, AnonymizationTask> create(Tuple2<Column2, AnonymizationTask> tuple2);
}
