package com.anonymization.etl.core;

import com.anonymization.etl.ColumnTupleRedisCodec;
import com.anonymization.etl.domain.ColumnTuple;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisUtils {

    public static RedisCommands<String, ColumnTuple> buildRedisCommands(String redisUrl) {
        RedisClient redisClient = RedisClient.create(redisUrl);
        StatefulRedisConnection<String, ColumnTuple> connection = redisClient.connect(new ColumnTupleRedisCodec());
        return connection.sync();
    }
}