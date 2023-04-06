package com.travis.filesbottle.member;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @ClassName MemberApplication
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/5
 */
@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication
public class MemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(MemberApplication.class, args);
    }
}
