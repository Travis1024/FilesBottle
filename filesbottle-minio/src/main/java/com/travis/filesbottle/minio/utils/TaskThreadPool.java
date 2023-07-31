package com.travis.filesbottle.minio.utils;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName TaskThreadPool
 * @Description 异步任务线程池配置
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
@Component
public class TaskThreadPool {

    @Value("${mythread.corePoolSize}")
    private Integer corePoolSize;

    @Value("${mythread.maximumPoolSize}")
    private Integer maximumPoolSize;

    @Value("${mythread.keepAliveTime}")
    private Long keepAliveTime;

    @Value("${mythread.maxBlockingCapacity}")
    private Integer maxBlockingCapacity;

    @Bean
    public ThreadPoolExecutor creatThreadPool() {

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                // 设置为守护线程
                .setDaemon(true)
                .setNamePrefix("FB-Docs-GeneratePreview---")
                .build();


        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(maxBlockingCapacity),
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }


}
