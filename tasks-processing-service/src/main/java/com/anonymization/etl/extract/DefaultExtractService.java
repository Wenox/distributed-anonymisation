package com.anonymization.etl.extract;

import com.anonymization.etl.core.BroadcastFacade;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.Task;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import scala.Tuple2;
import java.net.URI;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultExtractService implements ExtractService {

    @Value("${restoration-service.column-tuples.endpoint}")
    private String endpoint;

    @Override
    public Tuple2<ColumnTuple, Task> extract(Task task, BroadcastFacade broadcastFacade) {
        log.info("-----> Step 1: – extracting column tuple for task: {}", task);
        ColumnTuple columnTuple = fetchColumnTuple(task, broadcastFacade);
        sendExtractionSuccessNotification(broadcastFacade, task.getTaskId());
        return Tuple2.apply(columnTuple, task);
    }

    private ColumnTuple fetchColumnTuple(Task task, BroadcastFacade broadcastFacade) {
        String redisKey = buildRedisKey(task);
        return getColumnTupleFromRedis(broadcastFacade, redisKey)
                .orElseGet(() -> fetchAndCacheColumnTupleFromApi(broadcastFacade, task, redisKey));
    }

    private void sendExtractionSuccessNotification(BroadcastFacade broadcastFacade, String taskId) {
        broadcastFacade.getKafkaSinkBroadcast().getValue().send(KafkaConstants.TOPIC_EXTRACTION_SUCCESS, taskId);
    }

    private String buildRedisKey(Task task) {
        return String.join(":", task.getTableName(), task.getColumnName(), task.getBlueprintId());
    }

    private Optional<ColumnTuple> getColumnTupleFromRedis(BroadcastFacade broadcastFacade, String redisKey) {
        return Optional.ofNullable(broadcastFacade.redis().get(redisKey));
    }

    private ColumnTuple fetchAndCacheColumnTupleFromApi(BroadcastFacade broadcastFacade, Task task, String redisKey) {
        ColumnTuple columnTuple = fetchColumnTupleFromApi(task, broadcastFacade);
        broadcastFacade.redis().set(redisKey, columnTuple);

        return columnTuple;
    }

    private ColumnTuple fetchColumnTupleFromApi(Task task, BroadcastFacade broadcastFacade) {
        return broadcastFacade.webClient()
                .getWebClient()
                .get()
                .uri(uriBuilder -> buildColumnTupleUri(uriBuilder, task))
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    log.error("Failed to fetch column tuple from restoration servce: {}", response.statusCode());
                    return Mono.error(new RuntimeException("Error fetching column tuple from Restoration Service API"));
                })
                .bodyToMono(ColumnTuple.class)
                .publishOn(Schedulers.boundedElastic())
                .block();
    }

    private URI buildColumnTupleUri(UriBuilder uriBuilder, Task task) {
        return uriBuilder.path(endpoint)
                .queryParam("blueprint_id", task.getBlueprintId())
                .queryParam("table", task.getTableName())
                .queryParam("column", task.getColumnName())
                .queryParam("pk", task.getPrimaryKey())
                .build();
    }
}
