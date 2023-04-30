package com.anonymization.etl.core;

import software.amazon.awssdk.services.s3.S3Client;
import com.wenox.anonymization.s3.S3Constants;
import lombok.extern.slf4j.Slf4j;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.Serializable;
import java.util.function.Supplier;

@Slf4j
public class S3Sink implements Serializable {

    private final Supplier<S3Client> s3ClientSupplier;
    private transient S3Client s3Client;

    public S3Sink(Supplier<S3Client> s3ClientSupplier) {
        this.s3ClientSupplier = s3ClientSupplier;
    }

    private S3Client getS3Client() {
        if (s3Client == null) {
            log.info("Preparing for AmazonS3 instantiation...");
            s3Client = s3ClientSupplier.get();
        }
        return s3Client;
    }

    public void upload(String key, String bucket, byte[] data) {
        log.info("Uploading to S3 | Bucket: {} | Key: {}", bucket, key);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(S3Constants.BUCKET_BLUEPRINTS)
                .key(key)
                .build();

        RequestBody requestBody = RequestBody.fromBytes(data);

        PutObjectResponse response = getS3Client().putObject(putObjectRequest, requestBody);
    }

    public static S3Sink apply(BroadcastSettings config) {
        return new S3Sink(new AmazonS3ClientSupplier(config));
    }

    public static ClassTag<S3Sink> getClassTag() {
        return ClassTag$.MODULE$.apply(S3Sink.class);
    }
}
