package com.travis.filesbottle.minio.entity.bo;

import com.travis.filesbottle.minio.entity.Document;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName MinioUploadInfo
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/6
 */
@Data
public class MinioUploadInfo {
    private String uploadId;
    private LocalDateTime expireTime;
    private List<String> urlList;
    private Document document;
}
