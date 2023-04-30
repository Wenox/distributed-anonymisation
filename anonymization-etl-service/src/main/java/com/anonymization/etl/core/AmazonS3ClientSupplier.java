package com.anonymization.etl.core;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.Serializable;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class AmazonS3ClientSupplier implements Supplier<S3Client>, Serializable {

    private final BroadcastSettings config;

    @Override
    public S3Client get() {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(config.getAwsAccessKeyId(), config.getAwsSecretAccessKey())))
                .region(Region.of(config.getAwsRegion()))
                .build();
    }
}
