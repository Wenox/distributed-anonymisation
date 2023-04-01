package com.wenox.anonymization.s3_file_manager.api;

import java.io.InputStream;

public interface StorageService {

    void uploadFile(String bucketName, String objectName, InputStream inputStream);

    InputStream downloadFile(String bucketName, String objectName);
}
