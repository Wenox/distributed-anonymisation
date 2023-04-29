package com.anonymization.etl.transform.operations;

import com.anonymization.etl.core.KafkaSink;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import com.anonymization.etl.domain.OperationType;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.broadcast.Broadcast;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.io.Serializable;

@Service
@Slf4j
public class DefaultTransformService implements TransformService, Serializable {

    @Override
    public Tuple2<ColumnTuple, AnonymizationTask> anonymize(Tuple2<ColumnTuple, AnonymizationTask> input,
                                                            Broadcast<KafkaSink> kafkaSinkBroadcast) {
        log.info("Transforming...");

        kafkaSinkBroadcast.getValue().send(KafkaConstants.TOPIC_TRANSFORMATION_SUCCESS, input._2.getTaskId());

        AnonymizationTask task = input._2;
        OperationType type = task.getType();

        switch (type) {
            case SUPPRESSION:
                return transformSuppressionTask(input);
            case SHUFFLE:
                return transformShuffleTask(input);
            default:
                log.info("Unsupported type! Value: {}", task);
                return input;
        }
    }

    private Tuple2<ColumnTuple, AnonymizationTask> transformSuppressionTask(Tuple2<ColumnTuple, AnonymizationTask> input) {
        log.info("Transforming suppressionTask: {}", input._2);
        // Add your suppression task transformation logic here
        return input;
    }

    private Tuple2<ColumnTuple, AnonymizationTask> transformShuffleTask(Tuple2<ColumnTuple, AnonymizationTask> input) {
        log.info("Transforming shuffleTask: {}", input._2);
        // Add your shuffle task transformation logic here
        return input;
    }
}
