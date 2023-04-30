package com.anonymization.etl.load;


import com.anonymization.etl.core.S3Sink;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import com.anonymization.etl.transform.script.Column2Script;
import com.anonymization.etl.domain.SuccessEvent;
import com.wenox.anonymization.s3.S3Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.broadcast.Broadcast;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.io.Serializable;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadService implements Serializable {

    public SuccessEvent load(Tuple2<Column2Script, AnonymizationTask> scriptTuple, Broadcast<S3Sink> s3SinkBroadcast) {
        log.info("Loading the partial file content (inside Column2Script) into store......");

        String key = String.format("%s/%s/%s/%s.sql", scriptTuple._2.getWorksheetId(), scriptTuple._2.getTableName(), scriptTuple._2.getColumnName(),
                scriptTuple._2.getType().name());

        log.info("Trying to upload to key: {}", key);
        s3SinkBroadcast.getValue().upload(key, S3Constants.BUCKET_BLUEPRINTS, scriptTuple._1.getContent().getBytes());

        log.info("Loaded!");
        return new SuccessEvent(scriptTuple._2.getTaskId(), 0);
    }
}
