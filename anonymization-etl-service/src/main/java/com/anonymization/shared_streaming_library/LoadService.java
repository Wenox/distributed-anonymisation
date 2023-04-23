package com.anonymization.shared_streaming_library;


import com.anonymization.shared_streaming_library.poc.SuccessEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.io.Serializable;

@Slf4j
@Service
public class LoadService implements Serializable {

    public SuccessEvent load(Tuple2<Column2Script, AnonymizationTask> scriptTuple) {
        log.info("Loading...");
        return new SuccessEvent(scriptTuple._2.getTaskId(), 0);
    }
}
