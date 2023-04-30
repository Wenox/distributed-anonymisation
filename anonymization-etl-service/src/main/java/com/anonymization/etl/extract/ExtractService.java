package com.anonymization.etl.extract;

import com.anonymization.etl.core.BroadcastFacade;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import scala.Tuple2;

import java.io.Serializable;

public interface ExtractService extends Serializable {

    Tuple2<ColumnTuple, AnonymizationTask> extract(AnonymizationTask task, BroadcastFacade broadcastFacade);
}
