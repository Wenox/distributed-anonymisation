package com.anonymization.etl.config;

import com.anonymization.etl.domain.ColumnTuple;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RedisConfig {

    @Value("${redis.url}")
    private String redisUrl;

    public RedisCommands<String, ColumnTuple> buildRedisCommands() {
        RedisClient redisClient = RedisClient.create(redisUrl);
        StatefulRedisConnection<String, ColumnTuple> connection = redisClient.connect(new ColumnTupleRedisCodec());
        return connection.sync();
    }

}
