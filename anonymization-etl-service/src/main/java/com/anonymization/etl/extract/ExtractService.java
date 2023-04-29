package com.anonymization.etl.extract;

import com.anonymization.etl.core.BroadcastFacade;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;
import scala.Tuple2;

import java.io.Serializable;

@Service
@Slf4j
public class ExtractService implements Serializable {

    public Tuple2<ColumnTuple, AnonymizationTask> extract(AnonymizationTask task,
                                                          BroadcastFacade broadcastFacade) {

        WebClient webClient = WebClient.create("http://localhost:8200");

        String redisKey = task.getTableName() + ":" + task.getColumnName() + ":" + task.getBlueprintId();
        ColumnTuple columnTuple = broadcastFacade.redis().get(redisKey);

        if (columnTuple == null) {
            columnTuple = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/v1/restorations/column-tuple")
                            .queryParam("blueprint_id", task.getBlueprintId())
                            .queryParam("table", task.getTableName())
                            .queryParam("column", task.getColumnName())
                            .build())
                    .retrieve()
                    .bodyToMono(ColumnTuple.class)
                    .publishOn(Schedulers.boundedElastic())
                    .block();

            broadcastFacade.redis().set(redisKey, columnTuple);
        }

        broadcastFacade.getKafkaSinkBroadcast().getValue().send(KafkaConstants.TOPIC_EXTRACTION_SUCCESS, "SUCCESS");

        return Tuple2.apply(columnTuple, task);
    }
}