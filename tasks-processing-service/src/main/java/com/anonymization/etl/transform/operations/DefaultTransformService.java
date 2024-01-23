package com.anonymization.etl.transform.operations;

import com.anonymization.etl.core.KafkaSink;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.Task;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.broadcast.Broadcast;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class DefaultTransformService implements TransformService {

    private final Random rng = new Random(System.currentTimeMillis());

    @Override
    public Tuple2<ColumnTuple, Task> anonymize(Tuple2<ColumnTuple, Task> input,
                                               Broadcast<KafkaSink> kafkaSinkBroadcast) {
        log.info("-----> Step 2: Transforming - applying anonymisation for task: {}", input._2);

        Tuple2<ColumnTuple, Task> result;

        switch (input._2.getType()) {
            case SUPPRESSION -> result = transformSuppressionTask(input._1, input._2);
            case SHUFFLE -> result = transformShuffleTask(input._1, input._2);
            default -> {
                log.info("Unsupported type! Value: {}", input._2);
                result = input;
            }
        }

        publishTransformationSuccess(kafkaSinkBroadcast, input._2.getTaskId());
        return result;
    }

    private void publishTransformationSuccess(Broadcast<KafkaSink> kafkaSinkBroadcast, String taskId) {
        kafkaSinkBroadcast.getValue().send(KafkaConstants.TOPIC_TRANSFORMATION_ANONYMIZE_SUCCESS, taskId);
    }

    private Tuple2<ColumnTuple, Task> transformSuppressionTask(ColumnTuple columnTuple, Task task) {
        String token = String.valueOf(task.getConfiguration().get("token"));
        List<String> newValues = columnTuple.getValues()
                .stream()
                .map(value -> token)
                .toList();
        return Tuple2.apply(columnTuple.copyWithValues(newValues), task);
    }

    private Tuple2<ColumnTuple, Task> transformShuffleTask(ColumnTuple columnTuple, Task task) {
        boolean repetitions = Boolean.parseBoolean(task.getConfiguration().get("repetitions"));
        if (!repetitions) {
            Collections.shuffle(columnTuple.getValues());
            return Tuple2.apply(columnTuple, task);
        } else {
            var valuesAfterShuffle = shuffleWithRepetitions(columnTuple.getValues());
            return Tuple2.apply(columnTuple.copyWithValues(valuesAfterShuffle), task);
        }
    }

    private List<String> shuffleWithRepetitions(List<String> valuesBeforeShuffle) {
        final int valuesSize = valuesBeforeShuffle.size();
        List<String> afterShuffle = new ArrayList<>(valuesSize);
        for (int i = 0; i < valuesSize; i++) {
            afterShuffle.add(valuesBeforeShuffle.get(rng.nextInt(valuesSize)));
        }
        return afterShuffle;
    }
}
