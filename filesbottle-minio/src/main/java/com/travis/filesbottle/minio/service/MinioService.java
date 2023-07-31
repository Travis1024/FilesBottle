package com.travis.filesbottle.minio.service;

import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.minio.entity.Minio;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travis.filesbottle.minio.entity.bo.MinioGetUploadInfoParam;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author travis-wei
 * @since 2023-07-31
 */
public interface MinioService extends IService<Minio> {

    R<?> minioGetUploadId(MinioGetUploadInfoParam infoParam) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, ExecutionException, InvalidKeyException, InterruptedException, XmlParserException, InvalidResponseException, InternalException;

    R<?> minioCheckFileByMd5(String md5);

    R<?> uploadSingleDoc(String userId, String userName, String property, String description, MultipartFile file) throws IOException, InsufficientDataException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InternalException, ExecutionException, InterruptedException;
}
