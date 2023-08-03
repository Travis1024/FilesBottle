package com.travis.filesbottle.minio.entity.bo;

import lombok.Data;

/**
 * @ClassName GetMinioUploadInfoParam
 * @Description minio 请求分片上传文件的请求参数
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/7
 */
@Data
public class MinioGetUploadInfoParam {
    private String fileName;
    private String fileMd5;
    // 单位为 MB
    private Double fileSize;
    // 单位为 MB
    private Double chunkSize;
    private String contentType;
    // 文件描述
    private String fileDescription;
    // 文件属性
    private String fileProperty;
}
