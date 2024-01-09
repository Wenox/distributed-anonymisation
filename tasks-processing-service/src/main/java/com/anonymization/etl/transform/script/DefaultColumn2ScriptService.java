package com.anonymization.etl.transform.script;

import com.anonymization.etl.core.KafkaSink;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.broadcast.Broadcast;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Slf4j
public class DefaultColumn2ScriptService implements Column2ScriptService {

    @Override
    public Tuple2<Column2Script, AnonymizationTask> create(Tuple2<ColumnTuple, AnonymizationTask> tuple2,
                                                           Broadcast<KafkaSink> kafkaSinkBroadcast) {
        log.info("Creating script...");

        var task = tuple2._2;
        var columnTuple = tuple2._1;

        var pks = columnTuple.getPks();
        var values = columnTuple.getValues();

        List<String> updateQueries = IntStream.range(0, values.size())
                .mapToObj(i -> new Query.QueryBuilder(QueryType.UPDATE)
                        .tableName(task.getTableName())
                        .primaryKeyColumnName(task.getPrimaryKey())
                        .primaryKeyType(task.getPrimaryKeyType())
                        .primaryKeyValue(pks.get(i))
                        .columnName(task.getColumnName())
                        .columnType(task.getColumnType())
                        .columnValue(values.get(i))
                        .build()
                        .toString())
                .toList();

        Column2Script column2Script = new Column2Script();
        column2Script.setUpdateQueries(updateQueries);

        publishTransformationSuccess(kafkaSinkBroadcast, task.getTaskId());

        return Tuple2.apply(column2Script, tuple2._2);
    }

    private void publishTransformationSuccess(Broadcast<KafkaSink> kafkaSinkBroadcast, String taskId) {
        kafkaSinkBroadcast.getValue().send(KafkaConstants.TOPIC_TRANSFORMATION_SCRIPT_SUCCESS, taskId);
    }
}
