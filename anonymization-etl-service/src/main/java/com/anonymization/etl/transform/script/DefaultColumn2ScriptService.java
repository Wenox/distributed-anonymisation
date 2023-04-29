package com.anonymization.etl.transform.script;

import com.anonymization.etl.core.KafkaSink;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.broadcast.Broadcast;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.io.Serializable;

@Service
@Slf4j
public class DefaultColumn2ScriptService implements Column2ScriptService, Serializable {

    @Override
    public Tuple2<Column2Script, AnonymizationTask> create(Tuple2<ColumnTuple, AnonymizationTask> tuple2,
                                                           Broadcast<KafkaSink> kafkaSinkBroadcast) {
        log.info("Creating script...");
        return Tuple2.apply(new Column2Script("Script - content."), tuple2._2);
    }
}
