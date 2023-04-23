package com.anonymization.shared_streaming_library;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.io.Serializable;

@Service
@Slf4j
public class DefaultColumn2ScriptService implements Column2ScriptService, Serializable {

    @Override
    public Tuple2<Column2Script, AnonymizationTask> create(Tuple2<Column2, AnonymizationTask> tuple2) {
        log.info("Creating script...");
        return Tuple2.apply(new Column2Script("Script - content."), tuple2._2);
    }
}
