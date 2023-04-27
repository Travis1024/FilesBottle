package com.travis.filesbottle.report.service.document;

import com.travis.filesbottle.common.constant.RocketMqConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * @ClassName RmqDocViewServiceImpl
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/27
 */
@Slf4j
@Service
@RocketMQMessageListener(topic = RocketMqConstants.RMQ_TOPIC_DOCUMENT, consumerGroup = "RmqDocViewServiceImpl", selectorExpression = "view")
public class RmqDocViewServiceImpl implements RocketMQListener<String> {
    @Override
    public void onMessage(String s) {
        log.info("view 消费者成功接收到消息, {}", s);
    }
}
