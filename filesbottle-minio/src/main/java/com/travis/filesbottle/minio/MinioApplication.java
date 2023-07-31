package com.travis.filesbottle.minio;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @ClassName MinioApplication
 * @Description Minio文件模块启动类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/31
 */
@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication
public class MinioApplication {
    public static void main(String[] args) {
        SpringApplication.run(MinioApplication.class, args);
    }
}
