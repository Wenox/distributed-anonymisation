package com.wenox.anonymization.s3_file_manager.api;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {

    void uploadFile(MultipartFile file, String bucketName, String objectName) throws IOException;

    InputStream downloadFile(String bucketName, String objectName);
}
