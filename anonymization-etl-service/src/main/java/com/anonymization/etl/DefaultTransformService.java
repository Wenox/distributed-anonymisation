package com.anonymization.etl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.io.Serializable;

@Service
@Slf4j
public class DefaultTransformService implements TransformService, Serializable {

    @Override
    public Tuple2<Column2, AnonymizationTask> anonymize(Tuple2<Column2, AnonymizationTask> input) {
        log.info("Transforming...");

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

    private Tuple2<Column2, AnonymizationTask> transformSuppressionTask(Tuple2<Column2, AnonymizationTask> input) {
        log.info("Transforming suppressionTask: {}", input._2);
        // Add your suppression task transformation logic here
        return input;
    }

    private Tuple2<Column2, AnonymizationTask> transformShuffleTask(Tuple2<Column2, AnonymizationTask> input) {
        log.info("Transforming shuffleTask: {}", input._2);
        // Add your shuffle task transformation logic here
        return input;
    }
}
