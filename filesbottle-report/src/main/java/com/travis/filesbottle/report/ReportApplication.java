package com.travis.filesbottle.report;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @ClassName ReportApplication
 * @Description ReportApplication启动类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/26
 */
@EnableDiscoveryClient
@EnableDubbo
@SpringBootApplication
public class ReportApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReportApplication.class, args);
    }
}
