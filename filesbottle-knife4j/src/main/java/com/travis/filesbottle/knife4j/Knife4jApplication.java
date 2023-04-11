package com.travis.filesbottle.knife4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @ClassName Knife4jApplication
 * @Description Knife4jApplication
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/3
 */
@EnableDiscoveryClient
@SpringBootApplication
public class Knife4jApplication {
    public static void main(String[] args) {
        SpringApplication.run(Knife4jApplication.class, args);
    }
}
