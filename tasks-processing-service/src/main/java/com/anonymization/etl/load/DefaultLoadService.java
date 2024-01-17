package com.anonymization.etl.load;

import com.anonymization.etl.core.S3Sink;
import com.anonymization.etl.domain.tasks.Task;
import com.anonymization.etl.transform.script.Column2Script;
import com.wenox.anonymization.s3.S3Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.broadcast.Broadcast;
import org.springframework.stereotype.Service;
import scala.Tuple2;


@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultLoadService implements LoadService {

    public String load(Tuple2<Column2Script, Task> scriptTuple, Broadcast<S3Sink> s3SinkBroadcast) {
        log.info("-----> Step 4: – loading fragment into Amazon S3 for task: {}", scriptTuple._2);

        String key = String.format("%s/%s/%s/%s.sql", scriptTuple._2.getWorksheetId(), scriptTuple._2.getTableName(), scriptTuple._2.getColumnName(), scriptTuple._2.getType().name());

        byte[] byteArray = scriptTuple._1.toByteArray();
        s3SinkBroadcast.getValue().upload(key, S3Constants.BUCKET_FRAGMENTS, byteArray);

        log.info("Returning from load function");
        return scriptTuple._2.getTaskId();
    }
}
