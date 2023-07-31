package com.travis.filesbottle.minio.utils;

import com.google.common.collect.Multimap;
import io.minio.CreateMultipartUploadResponse;
import io.minio.ListPartsResponse;
import io.minio.MinioAsyncClient;
import io.minio.ObjectWriteResponse;
import io.minio.errors.*;
import io.minio.messages.Part;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

/**
 * @ClassName MyMinioAsyncClient
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/6
 */
public class CustomMinioAsyncClient extends MinioAsyncClient {

    protected CustomMinioAsyncClient(MinioAsyncClient client) {
        super(client);
    }

    @Override
    public CreateMultipartUploadResponse createMultipartUpload(String bucketName, String region, String objectName, Multimap<String, String> headers, Multimap<String, String> extraQueryParams) throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException, InternalException, InvalidResponseException {
        return super.createMultipartUpload(bucketName, region, objectName, headers, extraQueryParams);
    }

    @Override
    public ObjectWriteResponse completeMultipartUpload(String bucketName, String region, String objectName, String uploadId, Part[] parts, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException, InternalException, InvalidResponseException {
        return super.completeMultipartUpload(bucketName, region, objectName, uploadId, parts, extraHeaders, extraQueryParams);
    }

    @Override
    public ListPartsResponse listParts(String bucketName, String region, String objectName, Integer maxParts, Integer partNumberMarker, String uploadId, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException, InternalException, InvalidResponseException {
        return super.listParts(bucketName, region, objectName, maxParts, partNumberMarker, uploadId, extraHeaders, extraQueryParams);
    }


    /**
     * ------------------------------------------------------------------Async------------------------------------------------------------------
     */
    /**
     * @MethodName createMultipartUploadAsync
     * @Description 创建分片上传请求
     * @Author travis-wei
     * @Data 2023/7/6
     * @param bucketName	存储桶
     * @param region	    区域
     * @param objectName	对象名
     * @param headers	    消息头
     * @param extraQueryParams	额外查询参数
     * @Return java.util.concurrent.CompletableFuture<io.minio.CreateMultipartUploadResponse>
     **/
    @Override
    public CompletableFuture<CreateMultipartUploadResponse> createMultipartUploadAsync(String bucketName, String region, String objectName, Multimap<String, String> headers, Multimap<String, String> extraQueryParams) throws InsufficientDataException, InternalException, InvalidKeyException, IOException, NoSuchAlgorithmException, XmlParserException {
        return super.createMultipartUploadAsync(bucketName, region, objectName, headers, extraQueryParams);
    }

    /**
     * @MethodName completeMultipartUploadAsync
     * @Description 完成分片上传后，合并文件
     * @Author travis-wei
     * @Data 2023/7/6
     * @param bucketName    存储桶
     * @param region	    区域
     * @param objectName	对象名
     * @param uploadId	    上传 ID
     * @param parts	        分片
     * @param extraHeaders	额外消息头
     * @param extraQueryParams	额外查询参数
     * @Return java.util.concurrent.CompletableFuture<io.minio.ObjectWriteResponse>
     **/
    @Override
    public CompletableFuture<ObjectWriteResponse> completeMultipartUploadAsync(String bucketName, String region, String objectName, String uploadId, Part[] parts, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws InsufficientDataException, InternalException, InvalidKeyException, IOException, NoSuchAlgorithmException, XmlParserException {
        return super.completeMultipartUploadAsync(bucketName, region, objectName, uploadId, parts, extraHeaders, extraQueryParams);
    }


    /**
     * @MethodName listPartsAsync
     * @Description 查询分片数据
     * @Author travis-wei
     * @Data 2023/7/6
     * @param bucketName    存储桶
     * @param region        区域
     * @param objectName    对象名
     * @param maxParts
     * @param partNumberMarker
     * @param uploadId          上传 ID
     * @param extraHeaders      额外消息头
     * @param extraQueryParams  额外查询参数
     * @Return java.util.concurrent.CompletableFuture<io.minio.ListPartsResponse>
     **/
    @Override
    public CompletableFuture<ListPartsResponse> listPartsAsync(String bucketName, String region, String objectName, Integer maxParts, Integer partNumberMarker, String uploadId, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws InsufficientDataException, InternalException, InvalidKeyException, IOException, NoSuchAlgorithmException, XmlParserException {
        return super.listPartsAsync(bucketName, region, objectName, maxParts, partNumberMarker, uploadId, extraHeaders, extraQueryParams);
    }
}
