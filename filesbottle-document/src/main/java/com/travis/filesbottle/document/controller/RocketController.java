package com.travis.filesbottle.document.controller;

import com.travis.filesbottle.common.constant.RocketMqConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName RocketController
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/26
 */
@RestController
@Slf4j
@RequestMapping("/mq")
public class RocketController {

    // 直接注入使用，用于发送消息到 broker 服务器
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @PostMapping("/send")
    public void sendMq(@RequestParam("tag") String tag) {
        String destination = RocketMqConstants.RMQ_TOPIC_DOCUMENT + ":" + tag;
        log.info(destination);
        rocketMQTemplate.syncSend(destination, "发送" + tag);
    }

}
