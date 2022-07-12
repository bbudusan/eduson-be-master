package com.servustech.eduson.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
	
	@Value("${cloud.minio.endpoint}")
	private String endpoint;
	
	@Value("${cloud.minio.credentials.accessKey}")
	private String accessKey;
	
	@Value("${cloud.minio.credentials.secretKey}")
	private String secretKey;
	
	@Bean
	public MinioClient minioStorage() {
		System.out.println(endpoint);
		return MinioClient.builder()
						  .endpoint(endpoint)
						  .credentials(accessKey, secretKey)
						  .build();
	}
}
