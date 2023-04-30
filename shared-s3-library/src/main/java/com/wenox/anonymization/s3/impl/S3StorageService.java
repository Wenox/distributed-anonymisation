package com.wenox.anonymization.s3.impl;

import com.wenox.anonymization.s3.S3Constants;
import com.wenox.anonymization.s3.api.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

@Service
public class S3StorageService implements StorageService, Serializable {

    private final S3Client s3Client;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void uploadFile(MultipartFile file, String bucketName, String key) throws IOException {
        try (InputStream is = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(is, file.getSize());
            s3Client.putObject(putObjectRequest, requestBody);
        }
    }

    @Override
    public void uploadFile(byte[] data, String bucketName, String key) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        RequestBody requestBody = RequestBody.fromBytes(data);

        s3Client.putObject(putObjectRequest, requestBody);
    }

    @Override
    public InputStream downloadFile(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());
    }
}
