package com.anonymization.etl.extract;

import com.anonymization.etl.config.RedisConfig;
import com.anonymization.etl.core.RedisUtils;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExtractService implements Serializable {

    @Value("${redis.url}")
    private String redisUrl;

    public Iterator<Tuple2<ColumnTuple, AnonymizationTask>> extract(Iterator<AnonymizationTask> tasks) {
        RedisCommands<String, ColumnTuple> redisCommands = RedisUtils.buildRedisCommands(redisUrl);
        WebClient webClient = WebClient.create("http://localhost:8200");

        List<Tuple2<ColumnTuple, AnonymizationTask>> result = new ArrayList<>();

        tasks.forEachRemaining(task -> {
            String redisKey = task.getTableName() + ":" + task.getColumnName() + task.getWorksheetId();
            log.info("Generated Redis key: {}", redisKey);

            ColumnTuple columnTuple = redisCommands.get(redisKey);
            log.info("Value retrieved from Redis with key: {}, value: {}", redisKey, columnTuple);

            if (columnTuple == null) {
                log.info("Not found in redis, calling get to restoration service");

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

                log.info("Retrieved using GET: {}", columnTuple);

                redisCommands.set(redisKey, columnTuple);
                log.info("Value stored in Redis with key: {}, value: {}", redisKey, columnTuple);
            } else {
                log.info("Found in redis: {}", columnTuple);
            }

            result.add(Tuple2.apply(columnTuple, task));
        });

        return result.iterator();
    }

//    public Tuple2<ColumnTuple, AnonymizationTask> extract(AnonymizationTask task) {
//        WebClient webClient = WebClient.create("http://localhost:8200");
//
//        String redisKey = task.getTableName() + ":" + task.getColumnName() + task.getWorksheetId();
//        log.info("Generated Redis key: {}", redisKey);
//
//        ColumnTuple columnTuple = redisCommands.get(redisKey);
//        log.info("Value retrieved from Redis with key: {}, value: {}", redisKey, columnTuple);
//
//        if (columnTuple == null) {
//            log.info("Not found in redis, calling get to restoration service");
//
//            columnTuple = webClient.get()
//                    .uri(uriBuilder -> uriBuilder.path("/api/v1/restorations/column-tuple")
//                            .queryParam("blueprint_id", task.getBlueprintId())
//                            .queryParam("table", task.getTableName())
//                            .queryParam("column", task.getColumnName())
//                            .build())
//                    .retrieve()
//                    .bodyToMono(ColumnTuple.class)
//                    .publishOn(Schedulers.boundedElastic())
//                    .block();
//
//            log.info("Retrieved using GET: {}", columnTuple);
//
//            redisCommands.set(redisKey, columnTuple);
//            log.info("Value stored in Redis with key: {}, value: {}", redisKey, columnTuple);
//        } else {
//            log.info("Found in redis: {}", columnTuple);
//        }
//
//        return Tuple2.apply(columnTuple, task);
//    }
}
