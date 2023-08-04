package com.travis.filesbottle.minio.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.minio.entity.Document;
import com.travis.filesbottle.minio.entity.Minio;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travis.filesbottle.minio.entity.bo.MinioGetUploadInfoParam;
import com.travis.filesbottle.minio.entity.bo.MinioMergeParam;
import com.travis.filesbottle.minio.entity.bo.MinioUploadInfo;
import io.minio.errors.*;
import org.elasticsearch.search.SearchHit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
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

    R<?> minioGetUploadId(String userId, String userName, MinioGetUploadInfoParam infoParam) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, ExecutionException, InvalidKeyException, InterruptedException, XmlParserException, InvalidResponseException, InternalException;

    R<?> uploadSingleDoc(String userId, String userName, String property, String description, MultipartFile file) throws IOException, InsufficientDataException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InternalException, ExecutionException, InterruptedException;

    R<Document> mergeUploadParts(String userId, String userName, MinioMergeParam minioMergeParam) throws InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, ExecutionException, XmlParserException, InterruptedException, InternalException;

    R<?> listUploadChunkList(String objectName, String uploadId) throws InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, ExecutionException, XmlParserException, InterruptedException, InternalException;

    R<?> listAll(String userId);

    List<Document> selectAllListByPage(Page<Document> page, QueryWrapper<Document> queryWrapper);

    R<List<SearchHit>> esDocumentByKeyword(String keyword, String userId) throws IOException;

    ResponseEntity<Object> downloadSourceDocument(String objectName, String fileName, String userId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    R<?> deleteDocumentById(String sourceId, String userId);

    R<?> getPreviewStream(String sourceId, String userId, HttpServletResponse response) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}
