package com.anonymization.etl.load;


import com.anonymization.etl.domain.tasks.AnonymizationTask;
import com.anonymization.etl.transform.script.Column2Script;
import com.anonymization.etl.domain.SuccessEvent;
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
