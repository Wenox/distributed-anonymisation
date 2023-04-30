package com.anonymization.etl.core;

import com.anonymization.etl.domain.ColumnTuple;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.extern.slf4j.Slf4j;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Slf4j
public class RedisSink implements Serializable {

    private final Supplier<StatefulRedisConnection<String, ColumnTuple>> redisConnectionSupplier;
    private final AtomicReference<StatefulRedisConnection<String, ColumnTuple>> redisConnectionRef;

    public RedisSink(Supplier<StatefulRedisConnection<String, ColumnTuple>> redisConnectionSupplier) {
        this.redisConnectionSupplier = redisConnectionSupplier;
        this.redisConnectionRef = new AtomicReference<>();
    }

    private StatefulRedisConnection<String, ColumnTuple> getRedisConnection() {
        return redisConnectionRef.updateAndGet(currentRedisConnection -> {
            if (currentRedisConnection == null) {
                log.info("Preparing for Redis Connection instantiation...");
                return redisConnectionSupplier.get();
            }
            return currentRedisConnection;
        });
    }

    public ColumnTuple get(String key) {
        return getRedisConnection().sync().get(key);
    }

    public void set(String key, ColumnTuple value) {
        getRedisConnection().sync().set(key, value);
    }

    public static RedisSink apply(BroadcastSettings config) {
        return new RedisSink(new RedisConnectionSupplier(config));
    }

    public static ClassTag<RedisSink> getClassTag() {
        return ClassTag$.MODULE$.apply(RedisSink.class);
    }
}