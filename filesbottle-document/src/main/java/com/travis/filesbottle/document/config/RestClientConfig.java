package com.travis.filesbottle.document.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

/**
 * @ClassName RestClientConfig
 * @Description elasticSearch客户端“RestHighLevelClient”配置类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/13
 */
@Configuration
public class RestClientConfig extends AbstractElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String url;
    @Value("${spring.elasticsearch.connection-timeout}")
    private Long connectTimeout;
    @Value("${spring.elasticsearch.socket-timeout}")
    private Long socketTimeout;


    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(url)
                .withConnectTimeout(connectTimeout)
                .withSocketTimeout(socketTimeout)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
