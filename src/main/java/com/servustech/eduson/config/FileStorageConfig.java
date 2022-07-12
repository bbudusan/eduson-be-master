package com.servustech.eduson.config;

import com.amazonaws.services.s3.AmazonS3Client;
import com.servustech.eduson.utils.filestorage.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class FileStorageConfig {

    private final AmazonS3Client amazonS3Client;
    private final MinioConfig minioConfig;
    
    
    @Value("${spring.storage.type}")
    private StorageType storageType;

    @Value("${cloud.aws.bucket}")
    private String bucket;
    
    @Bean
    public FileStorage defaultFileStorage() {
        if (storageType.isAWS()) {
            return new AWSFileStorage(amazonS3Client, bucket);
        }
        if (storageType.isFileSystem()) {
            return new FSFileStorage();
        }
        
        return new MinioFileStorage(minioConfig.minioStorage());
    }

}
