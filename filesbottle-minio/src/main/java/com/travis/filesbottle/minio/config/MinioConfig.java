package com.travis.filesbottle.minio.config;

import com.travis.filesbottle.minio.utils.CustomMinioAsyncClient;
import com.travis.filesbottle.minio.utils.MinioProperties;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MinioConfig
 * @Description Minio客户端配置类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/6
 */
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {

    @Autowired
    private MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }
    @Bean
    public CustomMinioAsyncClient customMinioAsyncClient() {
        return (CustomMinioAsyncClient) CustomMinioAsyncClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

}
