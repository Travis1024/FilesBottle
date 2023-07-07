package com.travis.filesbottle.document.utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.TemporalUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.travis.filesbottle.document.entity.bo.MinioUploadInfo;
import io.minio.CreateMultipartUploadResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListPartsResponse;
import io.minio.ObjectWriteResponse;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import io.minio.messages.Part;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName MinioUtil
 * @Description Minio 工具类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/6
 */
@Slf4j
@Component
public class MinioUtil {
    @Autowired
    private MinioProperties minioProperties;
    @Autowired
    private CustomMinioAsyncClient minioAsyncClient;

    /**
     * @MethodName initMultiPartUpload
     * @Description 获取分片上传信息（uploadId、urls、expireTime 等）
     * @Author travis-wei
     * @Data 2023/7/6
     * @param objectName
     * @param partCount
     * @param contentType
     * @Return void
     **/
    public MinioUploadInfo initMultiPartUpload(String objectName, int partCount, String contentType) {

        List<String> urlList = new ArrayList<>();
        String uploadId = null;

        Multimap<String, String> headers = ArrayListMultimap.create();
        headers.put("Content-Type", contentType);

        // 获取 uploadId
        try {
            uploadId = this.getUploadId(minioProperties.getBucketName(), null, objectName, headers, null);
        } catch (Exception e) {
            log.error(e.toString());
        }

        // 获取上传 urlList
        HashMap<String, String> paramsMap = new HashMap<>(2);
        paramsMap.put("uploadId", uploadId);
        try {
            for (int i = 1; i <= partCount; i++) {
                paramsMap.put("partNumber", String.valueOf(i));
                String uploadUrl = minioAsyncClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.GET)
                                .bucket(minioProperties.getBucketName())
                                .object(objectName)
                                // 指定上传链接有效期
                                .expiry(minioProperties.getChunkUploadExpirySecond())
                                // paramsMap包含两项（uploadId:固定，partNumber:变化）
                                .extraQueryParams(paramsMap)
                                .build()
                );

                urlList.add(uploadUrl);
            }

        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException(e.getMessage());
        }

        LocalDateTime expireTime = LocalDateTimeUtil.offset(LocalDateTime.now(), minioProperties.getChunkUploadExpirySecond(), ChronoUnit.SECONDS);
        MinioUploadInfo minioUploadInfo = new MinioUploadInfo();
        minioUploadInfo.setUploadId(uploadId);
        minioUploadInfo.setExpireTime(expireTime);
        minioUploadInfo.setUrlList(urlList);
        return minioUploadInfo;
    }

    /**
     * @MethodName mergeUploadParts
     * @Description 文件分片合并
     * @Author travis-wei
     * @Data 2023/7/7
     * @param objectName
     * @param uploadId
     * @Return java.lang.String
     **/
    public String mergeUploadParts(String objectName, String uploadId) throws InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InternalException, ExecutionException, InterruptedException {
        CompletableFuture<ListPartsResponse> completableFuture = minioAsyncClient.listPartsAsync(minioProperties.getBucketName(), null, objectName, null, 0, uploadId, null, null);
        ListPartsResponse listPartsResponse = completableFuture.get();
        if (listPartsResponse == null) {
            log.error("查询文件分片列表为空！");
            throw new RuntimeException("分片列表为空！");
        }
        Part[] parts = new Part[listPartsResponse.result().partList().size()];
        for (int index = 0; index < listPartsResponse.result().partList().size(); index++) {
            parts[index] = new Part(index + 1, listPartsResponse.result().partList().get(index).etag());
        }
        ObjectWriteResponse objectWriteResponse = null;
        try {
            CompletableFuture<ObjectWriteResponse> uploadAsync = minioAsyncClient.completeMultipartUploadAsync(minioProperties.getBucketName(), null, objectName, uploadId, parts, null, null);
            objectWriteResponse = uploadAsync.get();
        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException(e.getMessage());
        }
        if (objectWriteResponse == null) {
            log.error("合并失败，合并结果为空！");
            throw new RuntimeException("文件合并失败！");
        }
        return objectWriteResponse.region();
    }



    /**
     * @MethodName getUploadId
     * @Description 获取分片上传 upload ID
     * @Author travis-wei
     * @Data 2023/7/6
     * @param bucketName
     * @param region
     * @param objectName
     * @param headers
     * @param extraQueryParams
     * @Return java.lang.String
     **/
    private String getUploadId(String bucketName, String region, String objectName, Multimap<String, String> headers, Multimap<String, String> extraQueryParams) throws ExecutionException, InterruptedException, InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InternalException {
        CompletableFuture<CreateMultipartUploadResponse> future = minioAsyncClient.createMultipartUploadAsync(bucketName, region, objectName, headers, extraQueryParams);
        return future.get().result().uploadId();
    }

}
