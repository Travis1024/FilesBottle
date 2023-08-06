package com.travis.filesbottle.minio.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travis.filesbottle.common.constant.Constants;
import com.travis.filesbottle.common.dubboservice.ffmpeg.DubboFfmpegService;
import com.travis.filesbottle.common.dubboservice.member.DubboDocUpdateDataService;
import com.travis.filesbottle.common.dubboservice.member.DubboDocUserInfoService;
import com.travis.filesbottle.common.dubboservice.member.DubboUserInfoService;
import com.travis.filesbottle.common.dubboservice.member.bo.DubboDocumentUser;
import com.travis.filesbottle.common.enums.BizCodeEnum;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.minio.entity.Document;
import com.travis.filesbottle.minio.entity.EsDocument;
import com.travis.filesbottle.minio.entity.Minio;
import com.travis.filesbottle.minio.entity.bo.MinioGetUploadInfoParam;
import com.travis.filesbottle.minio.entity.bo.MinioMergeParam;
import com.travis.filesbottle.minio.entity.bo.MinioUploadInfo;
import com.travis.filesbottle.minio.mapper.MinioMapper;
import com.travis.filesbottle.minio.service.DocumentService;
import com.travis.filesbottle.minio.service.MinioService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travis.filesbottle.minio.utils.CustomMinioAsyncClient;
import com.travis.filesbottle.minio.utils.FileTypeEnumUtil;
import com.travis.filesbottle.minio.utils.MinioProperties;
import com.travis.filesbottle.minio.utils.MinioUtil;
import io.minio.*;
import io.minio.errors.*;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author travis-wei
 * @since 2023-07-31
 */
@Service
@Slf4j
public class MinioServiceImpl extends ServiceImpl<MinioMapper, Minio> implements MinioService {

    @Autowired
    private MinioProperties minioProperties;
    @Autowired
    private CustomMinioAsyncClient minioAsyncClient;
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${kkfileview.delete.urlprefix}")
    private String kkFileDeletePrefixUrl;
    @Value("${kkfileview.delete.password}")
    private String kkFileDeletePassword;

    @DubboReference
    private DubboDocUserInfoService dubboDocUserInfoService;
    @DubboReference
    private DubboDocUpdateDataService dubboDocUpdateDataService;
    @DubboReference
    private DubboUserInfoService dubboUserInfoService;
    @DubboReference
    private DubboFfmpegService dubboFfmpegService;


    @Override
    public R<?> minioGetUploadId(String userId, String userName, MinioGetUploadInfoParam infoParam) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, ExecutionException, InvalidKeyException, InterruptedException, XmlParserException, InvalidResponseException, InternalException {

        /**
         * 检查当前用户状态
         */
        DubboDocumentUser userInfo = dubboDocUserInfoService.getDocumentUserInfo(userId);
        if (userInfo == null) return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "该用户信息不存在！");
        if (userInfo.getUserBanning() == 1) return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.FORBIDDEN, "该用户已被封禁，无文件上传权限，请联系管理员！");

        /**
         * (检查) 根据文件 MD5 是否存在相同的文件
         */
        R<?> searchedFileByMd5 = documentService.searchFileByMd5(infoParam.getFileMd5(), userInfo.getUserTeamId());
        if (!R.checkSuccess(searchedFileByMd5)) return searchedFileByMd5;
        Document document = (Document) searchedFileByMd5.getData();
        if (document != null) return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, "该文件已在团队文件中存在！");

        // 获取文件名的后缀，并全部小写
        String suffix = infoParam.getFileName().substring(infoParam.getFileName().lastIndexOf('.') + 1).toLowerCase();
        // 生成文档 id
        String minioId = IdUtil.randomUUID();

        // 计算需要分片的数量，向上取整
        double partCount = Math.ceil(infoParam.getFileSize() / 1048576.0 / infoParam.getChunkSize());
        MinioUploadInfo minioUploadInfo = minioUtil.initMultiPartUpload(minioId + "." + suffix, (int) partCount, infoParam.getContentType());
        minioUploadInfo.setMinioId(minioId);
        return R.success(minioUploadInfo);
    }


    /**
     * @MethodName uploadSingleDoc
     * @Description 向 minio 文件系统中上传单个文件
     * @Author travis-wei
     * @Data 2023/7/31
     * @param userId
     * @param userName
     * @param property
     * @param description
     * @param file
     * @Return com.travis.filesbottle.common.utils.R<?>
     **/
    @Override
    public R<?> uploadSingleDoc(String userId, String userName, String property, String description, MultipartFile file) throws IOException, InsufficientDataException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InternalException, ExecutionException, InterruptedException {
        /**
         * 检查originalFilename是否为空
         */
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isEmpty(originalFilename)) {
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "无法获取文件名，请检查文件！");
        }

        /**
         * 检查当前用户状态
         */
        DubboDocumentUser userInfo = dubboDocUserInfoService.getDocumentUserInfo(userId);
        if (userInfo == null) return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "该用户信息不存在！");
        if (userInfo.getUserBanning() == 1) return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.FORBIDDEN, "该用户已被封禁，无文件上传权限，请联系管理员！");

        /**
         * (检查) 根据文件 MD5 是否存在相同的文件
         */
        String md5 = SecureUtil.md5(file.getInputStream());
        R<?> searchedFileByMd5 = documentService.searchFileByMd5(md5, userInfo.getUserTeamId());
        if (!R.checkSuccess(searchedFileByMd5)) return searchedFileByMd5;
        Document document = (Document) searchedFileByMd5.getData();
        if (document != null) return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, "该文件已在团队文件中存在！");


        /**
         * 封装文件信息
         */
        // 获取文件名的后缀，并全部小写
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        document = new Document();
        // 计算文件大小，单位为 MB（1024 * 1024 = 1048576）
        double docSize = file.getSize() / 1048576.0;
        document.setDocSize(docSize);
        document.setDocName(originalFilename);
        document.setDocMd5(md5);
        document.setDocUploadDate(new Timestamp(new Date().getTime()));
        // 根据文件后缀获取文件的类型码
        document.setDocFileTypeCode(FileTypeEnumUtil.getCodeBySuffix(suffix));
        document.setDocSuffix(suffix);
        document.setDocDescription(description);
        // 为 minio 文件获取 uuid
        String minioId = IdUtil.randomUUID();
        document.setDocMinioId(minioId);
        document.setDocContentTypeText(file.getContentType());
        document.setDocUserid(userId);
        document.setDocTeamid(userInfo.getUserTeamId());
        document.setDocProperty(property);

        /**
         * 上传文件到 minio 文件系统
         */
        CompletableFuture<ObjectWriteResponse> completableFuture = minioAsyncClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(minioId + "." + suffix)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        /**
         * 更新 mysql 数据库数据，开启事务
         */
        int result = updateMysqlDataWhenUpload(userId, userInfo.getUserTeamId(), property, document);
        // 数据更新失败
        if (result == 0) {
            if (completableFuture.isDone()) {
                minioAsyncClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(minioProperties.getBucketName())
                                .object(minioId + "." + suffix)
                                .build()
                );
            } else {
                completableFuture.cancel(true);
            }
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, "mysql数据更新失败，文件上传失败！");
        } else {
            completableFuture.get();
        }

        return R.success("文件上传成功！", document);
    }

    @Override
    public R<Document> mergeUploadParts(String userId, String userName, MinioMergeParam minioMergeParam) throws InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, ExecutionException, XmlParserException, InterruptedException, InternalException {

        MinioGetUploadInfoParam infoParam = minioMergeParam.getMinioGetUploadInfoParam();

        // 查询用户所属团队信息
        String teamId = dubboUserInfoService.getUserTeamId(userId);
        if (StrUtil.isEmpty(teamId)) return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "当前用户所属团队查询失败！");

        /**
         * 封装文件信息
         */
        // 获取文件名的后缀，并全部小写
        String suffix = infoParam.getFileName().substring(infoParam.getFileName().lastIndexOf('.') + 1).toLowerCase();
        Document document = new Document();
        // 计算文件大小，单位为 MB（1024 * 1024 = 1048576）
        document.setDocSize(infoParam.getFileSize() / 1048576.0);
        document.setDocMd5(infoParam.getFileMd5());
        document.setDocName(infoParam.getFileName());
        document.setDocUploadDate(new Timestamp(new Date().getTime()));
        // 根据文件后缀获取文件的类型码
        document.setDocFileTypeCode(FileTypeEnumUtil.getCodeBySuffix(suffix));
        document.setDocSuffix(suffix);
        document.setDocDescription(infoParam.getFileDescription());
        // 为 minio 文件获取 uuid
        String minioId = IdUtil.randomUUID();
        document.setDocMinioId(minioId);
        document.setDocContentTypeText(infoParam.getContentType());
        document.setDocUserid(userId);
        document.setDocTeamid(teamId);
        document.setDocProperty(infoParam.getFileProperty());


        // 合并文件请求
        String objectName = minioMergeParam.getMinioId() + "." + suffix;
        String merged = minioUtil.mergeUploadParts(objectName, minioMergeParam.getUploadId());
        if (StrUtil.isEmpty(merged)) throw new RuntimeException("合并文件异常！");


        // 更新 mysql 数据库中的数据信息
        int result = updateMysqlDataWhenUpload(userId, teamId, infoParam.getFileProperty(), document);
        // 数据更新失败
        if (result == 0) {
            minioAsyncClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .build()
            );
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, "mysql数据更新失败，文件上传失败！");
        }
        return R.success(document);
    }


    @Override
    public R<?> listUploadChunkList(String objectName, String uploadId) throws InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, ExecutionException, XmlParserException, InterruptedException, InternalException {
        List<Integer> chunkList = minioUtil.listUploadChunkList(objectName, uploadId);
        return R.success(chunkList);
    }

    @Override
    public R<?> listAll(String userId) {
        String userTeamId = dubboUserInfoService.getUserTeamId(userId);
        if (StrUtil.isEmpty(userTeamId)) throw new RuntimeException("未查询到当前用户的所属团队");
        List<Document> documentList = documentService.listAll(userTeamId);
        return R.success(documentList);
    }

    @Override
    public List<Document> selectAllListByPage(Page<Document> page, QueryWrapper<Document> queryWrapper) {
        Page<Document> documentPage = documentService.selectPage(page, queryWrapper);
        return documentPage.getRecords();
    }

    @Override
    public R<List<SearchHit>> esDocumentByKeyword(String keyword, String userId) throws IOException {
        // 查询当前用户所属团队的文件 list
        R<?> listed = listAll(userId);
        if (!R.checkSuccess(listed)) throw new RuntimeException(listed.getMessage());
        List<Document> documentList = (List<Document>) listed.getData();
        // 存储此团队所有文档 ID 的 set 集合
        Set<String> hashSet = new HashSet<>();
        for (Document document : documentList) {
            hashSet.add(document.getDocMinioId());
        }

        // ElasticSearch 多字段查询
        // 1、创建SearchRequest搜索请求，并指定要查询的索引
        SearchRequest searchRequest = new SearchRequest("document");
        // 2.1、创建SearchSourceBuilder条件构造
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 2.2、MultiMatch查找
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, EsDocument.FILE_NAME, EsDocument.FILE_DESCRIPTION);
        multiMatchQueryBuilder.operator(Operator.OR);
        searchSourceBuilder.query(multiMatchQueryBuilder);

        // 3、将SearchSourceBuilder添加到 SearchRequest中
        searchRequest.source(searchSourceBuilder);

        // 4、执行查询
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 5、输出查询时间
        log.info("ES查询时间为：" + searchResponse.getTook());

        SearchHit[] hits = searchResponse.getHits().getHits();
        LinkedList<SearchHit> list = new LinkedList<>();
        for (SearchHit hit : hits) {
            // 获取文件ID
            String minioId = (String) hit.getSourceAsMap().get(EsDocument.MINIO_ID);
            // 如果团队文档中包含此文档的ID信息，则加入list中，并返回
            if (hashSet.contains(minioId)) {
                list.add(hit);
            }
        }
        return R.success(list);
    }

    @Override
    public ResponseEntity<Object> downloadSourceDocument(String objectName, String fileName, String userId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        // 检查用户操作权限
        String minioId = objectName.substring(0, objectName.lastIndexOf('.'));
        boolean checked = checkUserAndDocTeam(userId, minioId);
        if (!checked) {
            throw new RuntimeException("当前用户无权限操作此文档!");
        }

        InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(objectName)
                        .build()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName=" + URLEncoder.encode(fileName, Constants.UTF8))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(inputStream));
    }

    @Override
    public R<?> deleteDocumentById(String sourceId, String userId) {
        // 查询文档的详细信息
        Document document = documentService.getDocInfoById(sourceId);
        if (document == null) throw new RuntimeException("未查询到文档信息！");
        if (StrUtil.isEmpty(document.getDocUserid())) throw new RuntimeException("未查询到文档创建者信息！");
        if (!document.getDocUserid().equals(userId)) throw new RuntimeException("无权限，文档只能由创建者删除！");

        // 获取文档类型码，根据文件类型码进行分步处理
        Short typeCode = document.getDocFileTypeCode();
        if (typeCode >= 1 && typeCode <= 200) {
            // 支持转为 pdf 进行预览的文件
            // (1) 删除 minio 源文件
            R<?> r1 = deleteMinioFile(sourceId + "." + document.getDocSuffix());
            // (2) 删除 minio 预览文件
            R<?> r2 = deleteMinioFile(document.getDocPreviewId() + "." + document.getDocSuffix());
            // (3) 删除 ElasticSearch 记录
            R<?> r3 = deleteEsRecord(sourceId);
            // (4) 删除 mysql 数据
            R<?> r4 = deleteMysqlRecord(sourceId);
            // (5) 判断是否均处理成功
            if (!R.checkSuccess(r1) || !R.checkSuccess(r2) || !R.checkSuccess(r3) || !R.checkSuccess(r4)) {
                return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, r1.getMessage() + r2.getMessage() + r3.getMessage() + r4.getMessage());
            }

        } else if (typeCode >= 351 && typeCode <= 400) {
            // 视频切片文件
            // (1) 删除 minio 源文件
            R<?> r1 = deleteMinioFile(sourceId + "." + document.getDocSuffix());
            // (2) 删除 视频切片文件
            boolean deleteVideo = dubboFfmpegService.deleteVideo(sourceId);
            // (3) 删除 ElasticSearch 记录
            R<?> r3 = deleteEsRecord(sourceId);
            // (4) 删除 mysql 数据
            R<?> r4 = deleteMysqlRecord(sourceId);
            // (5) 判断是否均处理成功
            if (!R.checkSuccess(r1) || deleteVideo || !R.checkSuccess(r3) || !R.checkSuccess(r4)) {
                return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, r1.getMessage() + r3.getMessage() + r4.getMessage());
            }

        } else if (typeCode >= 401 && typeCode <= 600) {
            // 支持使用 kkFileView 进行在线预览的文件
            // (1) 删除 minio 源文件
            R<?> r1 = deleteMinioFile(sourceId + "." + document.getDocSuffix());
            // (2) 删除 kkFileView 预览文件
            R<?> r2 = deleteKkFileById(sourceId, document.getDocSuffix());
            // (3) 删除 ElasticSearch 记录
            R<?> r3 = deleteEsRecord(sourceId);
            // (4) 删除 mysql 数据
            R<?> r4 = deleteMysqlRecord(sourceId);
            // (5) 判断是否均处理成功
            if (!R.checkSuccess(r1) || !R.checkSuccess(r2) || !R.checkSuccess(r3) || !R.checkSuccess(r4)) {
                return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, r1.getMessage() + r2.getMessage() + r3.getMessage() + r4.getMessage());
            }
        } else {
            // 不支持在线预览的文件 or 源文件自身可以预览的文件 or 未知类型的文件
            // (1) 删除 minio 源文件
            R<?> r1 = deleteMinioFile(sourceId + "." + document.getDocSuffix());
            // (2) 删除 ElasticSearch 记录
            R<?> r2 = deleteEsRecord(sourceId);
            // (3) 删除 mysql 数据
            R<?> r3 = deleteMysqlRecord(sourceId);
            // (4) 判断是否均处理成功
            if (!R.checkSuccess(r1) || !R.checkSuccess(r2) || !R.checkSuccess(r3)) {
                return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, r1.getMessage() + r2.getMessage() + r3.getMessage());
            }
        }
        return R.success();
    }

    @Override
    public R<?> getPreviewStream(String sourceId, String userId, HttpServletResponse response) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 一、首先查找该源文件信息是否存在，如果不存在直接返回文件不存在的 error 信息
        Document document = documentService.getDocInfoById(sourceId);
        if (document == null) throw new RuntimeException("未找到该文件信息！");

        // 二、[情况一：文件不支持在线预览] 判断该源文件的类型是否支持在线预览，如果不支持在线预览，返回状态码 18905 (document模块 + 不支持预览)
        Short typeCode = document.getDocFileTypeCode();
        if (typeCode == null || typeCode == 0 || typeCode == -1 || (typeCode >= 601 && typeCode <= 1000)) {
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.FILE_NOT_SUPPORT_PREVIEW);
        }
        // 三、分别处理支持预览的文件信息

        if (typeCode >= 1 && typeCode <= 200) {
            // [情况二：文件支持 pdf 预览文件预览]
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            // 获取源文件流
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(document.getDocPreviewId() + "." + document.getDocSuffix())
                            .build()
            );
            OutputStream outputStream = response.getOutputStream();
            byte[] bytes = new byte[4096];
            while ((inputStream.read(bytes)) != -1) {
                outputStream.write(bytes);
            }
            outputStream.flush();
            inputStream.close();
            outputStream.close();
        } else if (typeCode >= 201 && typeCode <= 350) {
            // [情况三：文件支持源文件在线预览，返回源文件流]
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(document.getDocContentTypeText());
            // 获取源文件流
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(document.getDocMinioId() + "." + document.getDocSuffix())
                            .build()
            );
            OutputStream outputStream = response.getOutputStream();
            byte[] bytes = new byte[4096];
            while ((inputStream.read(bytes)) != -1) {
                outputStream.write(bytes);
            }
            outputStream.flush();
            inputStream.close();
            outputStream.close();
        } else if (typeCode >= 351 && typeCode <= 400) {
            // [情况四：ffmpeg 视频文件在线预览] 视频文件预览请求
            String videoUrl = dubboFfmpegService.getVideoUrl(sourceId, userId);
            if (StrUtil.isEmpty(videoUrl)) throw new RuntimeException("获取视频预览地址失败！");
            return R.success(videoUrl);
        } else if (typeCode >= 401 && typeCode <= 600) {
            // [情况五：文件支持 kkFileView 在线预览]
            if (StrUtil.isEmpty(document.getDocPreviewUrl())) throw new RuntimeException("该文件多对应的预览文件获取失败！");
            return R.success(document.getDocPreviewUrl());
        }

        return R.success();
    }

    private R<?> deleteKkFileById(String sourceId, String suffix) {
        try {
            String fileName = Base64.encode(sourceId + "." + suffix);
            // 拼接完整字符串
            String resultUrl = kkFileDeletePrefixUrl + fileName + "&password" + kkFileDeletePassword;
            // 发送删除 kkfile 文件请求
            String forObject = restTemplate.getForObject(resultUrl, String.class);
            log.info(forObject);
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }
        return R.success();
    }

    private R<?> deleteMysqlRecord(String sourceId) {
        try {
            int delete = documentService.deleteMysqlRecord(sourceId);
            if (delete == 0) throw new RuntimeException("数据库-文档数据删除失败！");
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }
        return R.success();
    }

    private R<?> deleteEsRecord(String sourceId) {
        try {
            // 创建删除文档的请求，并指定索引和 id 值
            DeleteRequest deleteRequest = new DeleteRequest().index("document").id(sourceId);
            DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info(delete.toString());
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }
        return R.success();
    }


    private R<?> deleteMinioFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }
        return R.success();
    }

    /**
     * @MethodName updateMysqlDataWhenUpload
     * @Description 更新 mysql 数据库中的数据信息
     * @Author travis-wei
     * @Data 2023/7/31
     * @param userId
     * @param teamId
     * @param property
     * @param document
     * @Return java.lang.Integer
     **/
    @GlobalTransactional
    private Integer updateMysqlDataWhenUpload(String userId, String teamId, String property, Document document) {
        try {
            // 增加文件记录
            documentService.insertOne(document);
            // 增加个人文档数量
            dubboDocUpdateDataService.updateUserDocNumber(userId, property, "1");
            // 增加团队文档数量
            dubboDocUpdateDataService.updateTeamDocNumber(teamId, property, "1");

        } catch (Exception e) {
            log.error(e.toString());
            return 0;
        }
        return 1;
    }

    /**
     * @MethodName checkUserAndDocTeam
     * @Description 判断当前用户是否有权限处理当前文档
     * @Author travis-wei
     * @Data 2023/8/3
     * @param userId
     * @param minioId
     * @Return boolean
     **/
    private boolean checkUserAndDocTeam(String userId, String minioId) {
        String userTeamId = dubboUserInfoService.getUserTeamId(userId);
        if (StrUtil.isEmpty(userTeamId)) return false;
        Document document = documentService.getTeamIdByDocId(minioId);
        if ("public".equals(document.getDocProperty())) {
            return true;
        } else if ("private".equals(document.getDocProperty())) {
            return userTeamId.equals(document.getDocTeamid());
        }
        return false;
    }

}
