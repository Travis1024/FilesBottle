package com.travis.filesbottle.document.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.entity.bo.MinioGetUploadInfoParam;
import com.travis.filesbottle.document.entity.bo.MinioUploadInfo;
import com.travis.filesbottle.document.mapper.DocumentMapper;
import com.travis.filesbottle.document.service.MinioDocumentService;
import com.travis.filesbottle.document.utils.MinioUtil;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName MinioDocumentServiceImpl
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/7
 */
@Service
@Slf4j
public class MinioDocumentServiceImpl extends ServiceImpl<DocumentMapper, FileDocument> implements MinioDocumentService{

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private MinioUtil minioUtil;

    @Override
    public R<?> minioGetUploadId(MinioGetUploadInfoParam infoParam) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, ExecutionException, InvalidKeyException, InterruptedException, XmlParserException, InvalidResponseException, InternalException {
        // 计算需要分片的数量，向上取整
        double partCount = Math.ceil(infoParam.getFileSize() / infoParam.getChunkSize());
        MinioUploadInfo minioUploadInfo = minioUtil.initMultiPartUpload(infoParam.getFileName(), (int) partCount, infoParam.getContentType());

        if (minioUploadInfo != null) {
            // TODO
        }

        return R.success(minioUploadInfo);
    }
}
