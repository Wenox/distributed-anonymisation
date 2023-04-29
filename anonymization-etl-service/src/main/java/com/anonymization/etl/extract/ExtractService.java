package com.anonymization.etl.extract;

import com.anonymization.etl.config.RedisConfig;
import com.anonymization.etl.config.tests.KafkaProducerFactory;
import com.anonymization.etl.core.KafkaSink;
import com.anonymization.etl.core.RedisUtils;
import com.anonymization.etl.domain.ColumnTuple;
import com.anonymization.etl.domain.tasks.AnonymizationTask;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.broadcast.Broadcast;
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
public class ExtractService implements Serializable {

    private final String redisUrl;

    public ExtractService(@Value("${redis.url}") String redisUrl) {
        this.redisUrl = redisUrl;
    }

    public Iterator<Tuple2<ColumnTuple, AnonymizationTask>> extract(Iterator<AnonymizationTask> tasks, Broadcast<KafkaSink> kafkaSinkBroadcast) {
        List<Tuple2<ColumnTuple, AnonymizationTask>> result = new ArrayList<>();

        try (StatefulRedisConnection<String, ColumnTuple> redisCommands = RedisUtils.buildRedisCommands(redisUrl))

        {
            WebClient webClient = WebClient.create("http://localhost:8200");

            tasks.forEachRemaining(task -> {
                String redisKey = task.getTableName() + ":" + task.getColumnName() + ":" + task.getBlueprintId();
                log.info("Generated Redis key: {}", redisKey);

                ColumnTuple columnTuple = redisCommands.sync().get(redisKey);
                log.info("Value retrieved from Redis with key: {}, value: {}", redisKey, columnTuple);

                if (columnTuple == null) {
                    log.info("Not found in Redis, calling get to restoration service");

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

                    redisCommands.sync().set(redisKey, columnTuple);
                    log.info("Value stored in Redis with key: {}, value: {}", redisKey, columnTuple);
                }
                else {
                    log.info("@@@@@@@@@@@@@@@ !!!!!!! Found in Redis: {}", columnTuple);
                }

                log.info("SENDING TO KAFKA!!!@@@!!!!");
                kafkaSinkBroadcast.getValue().send(KafkaConstants.TOPIC_EXTRACTION_SUCCESS, "SUCCESS");


                result.add(Tuple2.apply(columnTuple, task));
            });
        }

        return result.iterator();
    }
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
