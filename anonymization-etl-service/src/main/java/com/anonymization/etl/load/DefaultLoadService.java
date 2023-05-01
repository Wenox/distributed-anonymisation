package com.anonymization.etl.load;

import com.anonymization.etl.core.S3Sink;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import com.anonymization.etl.transform.script.Column2Script;
import com.wenox.anonymization.s3.S3Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.broadcast.Broadcast;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultLoadService implements LoadService {

    public String load(Tuple2<Column2Script, AnonymizationTask> scriptTuple, Broadcast<S3Sink> s3SinkBroadcast) {
        String key = String.format("%s/%s/%s/%s.sql", scriptTuple._2.getWorksheetId() + LocalDateTime.now(), scriptTuple._2.getTableName(), scriptTuple._2.getColumnName(), scriptTuple._2.getType().name());

        byte[] byteArray = scriptTuple._1.toByteArray();
        s3SinkBroadcast.getValue().upload(key, S3Constants.BUCKET_BLUEPRINTS, byteArray);

        return scriptTuple._2.getTaskId();
    }
}
