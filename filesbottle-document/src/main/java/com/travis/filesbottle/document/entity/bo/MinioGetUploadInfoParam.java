package com.travis.filesbottle.document.entity.bo;

import lombok.Data;

/**
 * @ClassName GetMinioUploadInfoParam
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/7
 */
@Data
public class MinioGetUploadInfoParam {
    private String fileName;
    private String fileMd5;
    /**
     * 单位为 MB
     */
    private Double fileSize;
    /**
     * 单位为 MB
     */
    private Double chunkSize;
    private String contentType;
}
