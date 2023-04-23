package com.anonymization.shared_streaming_library;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.io.Serializable;

@Service
@Slf4j
public class DefaultTransformService implements TransformService, Serializable {

    @Override
    public Tuple2<Column2, AnonymizationTask> anonymize(Tuple2<Column2, AnonymizationTask> input) {
        log.info("Transforming...");
        return input;
    }
}
