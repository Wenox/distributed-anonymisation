package com.anonymization.etl.extract;

import com.anonymization.etl.core.BroadcastFacade;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import scala.Tuple2;
import java.net.URI;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultExtractService implements ExtractService {

    @Value("${restoration-service.column-tuple.endpoint}")
    private String endpoint;

    @Override
    public Tuple2<ColumnTuple, AnonymizationTask> extract(AnonymizationTask task, BroadcastFacade broadcastFacade) {
        ColumnTuple columnTuple = fetchColumnTuple(task, broadcastFacade);
        sendExtractionSuccessNotification(broadcastFacade);
        return Tuple2.apply(columnTuple, task);
    }

    private ColumnTuple fetchColumnTuple(AnonymizationTask task, BroadcastFacade broadcastFacade) {
        String redisKey = buildRedisKey(task);

        return getColumnTupleFromRedis(broadcastFacade, redisKey)
                .orElseGet(() -> fetchAndCacheColumnTupleFromApi(broadcastFacade, task, redisKey));
    }

    private void sendExtractionSuccessNotification(BroadcastFacade broadcastFacade) {
        broadcastFacade.getKafkaSinkBroadcast().getValue().send(KafkaConstants.TOPIC_EXTRACTION_SUCCESS, "SUCCESS");
    }

    private String buildRedisKey(AnonymizationTask task) {
        return String.join(":", task.getTableName(), task.getColumnName(), task.getBlueprintId());
    }

    private Optional<ColumnTuple> getColumnTupleFromRedis(BroadcastFacade broadcastFacade, String redisKey) {
        return Optional.ofNullable(broadcastFacade.redis().get(redisKey));
    }

    private ColumnTuple fetchAndCacheColumnTupleFromApi(BroadcastFacade broadcastFacade, AnonymizationTask task, String redisKey) {
        ColumnTuple columnTuple = fetchColumnTupleFromApi(task, broadcastFacade);
        broadcastFacade.redis().set(redisKey, columnTuple);

        return columnTuple;
    }

    private ColumnTuple fetchColumnTupleFromApi(AnonymizationTask task, BroadcastFacade broadcastFacade) {
        URI uri = buildColumnTupleUri(task);

        return broadcastFacade.webClient()
                .getWebClient()
                .get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    log.error("Failed to fetch column tuple from {}: {}", uri, response.statusCode());
                    return Mono.error(new RuntimeException("Error fetching column tuple from Restoration Service API"));
                })
                .bodyToMono(ColumnTuple.class)
                .publishOn(Schedulers.boundedElastic())
                .block();
    }

    private URI buildColumnTupleUri(AnonymizationTask task) {
        return UriComponentsBuilder.fromPath(endpoint)
                .queryParam("blueprint_id", task.getBlueprintId())
                .queryParam("table", task.getTableName())
                .queryParam("column", task.getColumnName())
                .build()
                .toUri();
    }
}
