package com.travis.filesbottle.minio.entity.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MinioMergeParam
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/8/3
 */
@Data
public class MinioMergeParam implements Serializable {
    private String uploadId;
    private String minioId;
    private MinioGetUploadInfoParam minioGetUploadInfoParam;
}
