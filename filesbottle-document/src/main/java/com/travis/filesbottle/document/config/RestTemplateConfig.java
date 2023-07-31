package com.travis.filesbottle.document.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName RestTemplateConfig
 * @Description 配置RestTemplate (发送请求)
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/19
 */
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        // 设置超时时间，单位为毫秒（20 分钟）
        simpleClientHttpRequestFactory.setReadTimeout(1200000);
        // 设置连接超时时间，单位为毫秒
        simpleClientHttpRequestFactory.setConnectTimeout(30000);
        return simpleClientHttpRequestFactory;
    }
}
