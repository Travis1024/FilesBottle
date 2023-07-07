package com.travis.filesbottle.document.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.entity.bo.MinioGetUploadInfoParam;
import io.minio.errors.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName MinioDocumentService
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/7
 */
public interface MinioDocumentService extends IService<FileDocument> {

    R<?> minioGetUploadId(MinioGetUploadInfoParam infoParam) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, ExecutionException, InvalidKeyException, InterruptedException, XmlParserException, InvalidResponseException, InternalException;

}
