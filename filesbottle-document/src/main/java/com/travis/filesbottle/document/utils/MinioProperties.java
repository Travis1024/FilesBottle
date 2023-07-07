package com.travis.filesbottle.document.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName MinioProperties
 * @Description minio 配置信息读取类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/6
 */
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private Integer chunkUploadExpirySecond;
}
