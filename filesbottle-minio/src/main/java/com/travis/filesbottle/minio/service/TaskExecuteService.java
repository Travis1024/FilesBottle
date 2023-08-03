package com.travis.filesbottle.minio.service;

import com.travis.filesbottle.minio.entity.Document;
import io.minio.errors.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName TaskExecuteService
 * @Description 异步任务实现接口
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
public interface TaskExecuteService {
    void generatePreviewFile(Long fileSize, Document document, InputStream inputStream) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}
