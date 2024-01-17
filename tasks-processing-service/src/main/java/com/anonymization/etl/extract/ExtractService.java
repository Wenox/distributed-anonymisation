package com.anonymization.etl.extract;

import com.anonymization.etl.core.BroadcastFacade;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.Task;
import scala.Tuple2;

import java.io.Serializable;

public interface ExtractService extends Serializable {

    Tuple2<ColumnTuple, Task> extract(Task task, BroadcastFacade broadcastFacade);
}
