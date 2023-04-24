package com.anonymization.etl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.io.Serializable;
import java.util.List;

@Service
@Slf4j
public class ExtractService implements Serializable {

    public Tuple2<Column2, AnonymizationTask> extract(AnonymizationTask task) {
        log.info("Extracting...");
        return Tuple2.apply(new Column2(List.of("PK-1", "PK-2"), List.of("VAL-1", "VAL-2")), task);
    }
}
