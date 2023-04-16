package com.travis.filesbottle.document.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MongoConfig
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/14
 */
@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.database}")
    private String mongodb;

    /**
     * @MethodName getGridFsBucket
     * @Description GridFSBucket用于打开下载流
     * @Author travis-wei
     * @Data 2023/4/14
     * @param mongoClient
     * @Return com.mongodb.client.gridfs.GridFSBucket
     **/
    @Bean
    public GridFSBucket getGridFsBucket(MongoClient mongoClient) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongodb);
        return GridFSBuckets.create(mongoDatabase);
    }
}
