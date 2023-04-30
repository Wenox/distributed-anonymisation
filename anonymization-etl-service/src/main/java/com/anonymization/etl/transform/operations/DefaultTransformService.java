package com.anonymization.etl.transform.operations;

import com.anonymization.etl.core.KafkaSink;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import com.anonymization.etl.domain.OperationType;
import com.anonymization.etl.domain.tasks.ShuffleTask;
import com.anonymization.etl.domain.tasks.SuppressionTask;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.broadcast.Broadcast;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class DefaultTransformService implements TransformService, Serializable {

    private final Random rng = new Random(System.currentTimeMillis());

    @Override
    public Tuple2<ColumnTuple, AnonymizationTask> anonymize(Tuple2<ColumnTuple, AnonymizationTask> input,
                                                            Broadcast<KafkaSink> kafkaSinkBroadcast) {
        log.info("Transforming task: {}", input._2);

        kafkaSinkBroadcast.getValue().send(KafkaConstants.TOPIC_TRANSFORMATION_SUCCESS, input._2.getTaskId());

        AnonymizationTask task = input._2;
        OperationType type = task.getType();

        switch (type) {
            case SUPPRESSION:
                return transformSuppressionTask(input._1, input._2);
            case SHUFFLE:
                return transformShuffleTask(input._1, input._2);
            default:
                log.info("Unsupported type! Value: {}", task);
                return input;
        }
    }

    private Tuple2<ColumnTuple, AnonymizationTask> transformSuppressionTask(ColumnTuple columnTuple, AnonymizationTask task) {
        String token = String.valueOf(task.getConfiguration().get("token"));
        System.out.println("token object: " + token);

        System.out.println("previous values: " + columnTuple.getValues());
        List<String> newValues = columnTuple.getValues()
                .stream()
                .map(value -> token)
                .toList();

        System.out.println("new values: " + newValues);

        return Tuple2.apply(columnTuple.copyWithValues(newValues), task);
    }

    private Tuple2<ColumnTuple, AnonymizationTask> transformShuffleTask(ColumnTuple columnTuple, AnonymizationTask task) {
        Boolean repetitions = Boolean.valueOf(task.getConfiguration().get("repetitions"));
        System.out.println("repetitions object: " + repetitions);

        if (!false) {
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
