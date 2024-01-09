package com.anonymization.etl.config;

import com.anonymization.etl.domain.ColumnTuple;
import io.lettuce.core.codec.RedisCodec;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ColumnTupleRedisCodec implements RedisCodec<String, ColumnTuple> {

    private final Charset charset = StandardCharsets.UTF_8;

    @Override
    public String decodeKey(ByteBuffer bytes) {
        return charset.decode(bytes).toString();
    }

    @Override
    public ColumnTuple decodeValue(ByteBuffer bytes) {
        try {
            byte[] array = new byte[bytes.remaining()];
            bytes.get(array);
            ByteArrayInputStream bis = new ByteArrayInputStream(array);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (ColumnTuple) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return charset.encode(key);
    }

    @Override
    public ByteBuffer encodeValue(ColumnTuple value) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(value);
            oos.flush();
            return ByteBuffer.wrap(bos.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
