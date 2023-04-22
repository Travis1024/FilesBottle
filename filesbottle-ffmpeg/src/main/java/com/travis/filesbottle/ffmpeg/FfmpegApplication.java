package com.travis.filesbottle.ffmpeg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @ClassName FfmpegApplication
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/21
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class FfmpegApplication {
    public static void main(String[] args) {
        SpringApplication.run(FfmpegApplication.class, args);
    }
}
