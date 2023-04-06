package com.wenox.anonymization.s3_file_manager.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.wenox.anonymization.s3_file_manager.api.StorageService;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
class S3StorageService implements StorageService {

    private final AmazonS3 amazonS3;

    public S3StorageService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public void uploadFile(String bucketName, String objectName, InputStream inputStream) {
        amazonS3.putObject(bucketName, objectName, inputStream, null);
    }

    @Override
    public InputStream downloadFile(String bucketName, String objectName) {
        S3Object s3Object = amazonS3.getObject(bucketName, objectName);
        return s3Object.getObjectContent();
    }
}
