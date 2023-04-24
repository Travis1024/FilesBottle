package com.travis.filesbottle.ffmpeg;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @ClassName FfmpegApplication
 * @Description FfmpegApplication启动类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/21
 */
@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class FfmpegApplication {
    public static void main(String[] args) {
        SpringApplication.run(FfmpegApplication.class, args);
    }
}
