package com.anonymization.etl.core;

import com.anonymization.etl.domain.ColumnTuple;
import io.lettuce.core.api.StatefulRedisConnection;

import java.io.Serializable;
import java.util.function.Supplier;

public class RedisConnectionSupplier implements Supplier<StatefulRedisConnection<String, ColumnTuple>>, Serializable {

    @Override
    public StatefulRedisConnection<String, ColumnTuple> get() {
        var connection = RedisUtils.buildRedisCommands("redis://localhost:6379");

        Runtime.getRuntime().addShutdownHook(new Thread(connection::close));

        return connection;
    }
}