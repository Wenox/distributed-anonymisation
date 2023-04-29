package com.anonymization.etl.core;

import com.anonymization.etl.domain.ColumnTuple;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.extern.slf4j.Slf4j;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

import java.io.Serializable;
import java.util.function.Supplier;

@Slf4j
public class RedisSink implements Serializable {

    private final Supplier<StatefulRedisConnection<String, ColumnTuple>> redisConnectionSupplier;
    private transient StatefulRedisConnection<String, ColumnTuple> redisConnection;

    public RedisSink(Supplier<StatefulRedisConnection<String, ColumnTuple>> redisConnectionSupplier) {
        this.redisConnectionSupplier = redisConnectionSupplier;
    }

    public StatefulRedisConnection<String, ColumnTuple> getRedisConnection() {
        if (redisConnection == null) {
            log.info("Preparing for RedisConnectionSupplier instantiation...");
            redisConnection = redisConnectionSupplier.get();
        }
        return redisConnection;
    }

    public static RedisSink apply() {
        return new RedisSink(new RedisConnectionSupplier());
    }

    public static ClassTag<RedisSink> getClassTag() {
        return ClassTag$.MODULE$.apply(RedisSink.class);
    }
}