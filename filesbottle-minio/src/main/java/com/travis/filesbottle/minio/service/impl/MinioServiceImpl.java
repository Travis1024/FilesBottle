package com.travis.filesbottle.minio.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.travis.filesbottle.common.dubboservice.member.DubboDocUpdateDataService;
import com.travis.filesbottle.common.dubboservice.member.DubboDocUserInfoService;
import com.travis.filesbottle.common.dubboservice.member.bo.DubboDocumentUser;
import com.travis.filesbottle.common.enums.BizCodeEnum;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.minio.entity.Document;
import com.travis.filesbottle.minio.entity.Minio;
import com.travis.filesbottle.minio.entity.bo.MinioGetUploadInfoParam;
import com.travis.filesbottle.minio.entity.bo.MinioUploadInfo;
import com.travis.filesbottle.minio.mapper.MinioMapper;
import com.travis.filesbottle.minio.service.DocumentService;
import com.travis.filesbottle.minio.service.MinioService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travis.filesbottle.minio.utils.CustomMinioAsyncClient;
import com.travis.filesbottle.minio.utils.FileTypeEnumUtil;
import com.travis.filesbottle.minio.utils.MinioProperties;
import com.travis.filesbottle.minio.utils.MinioUtil;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
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
public class MinioServiceImpl extends ServiceImpl<MinioMapper, Minio> implements MinioService {

    @Autowired
    private MinioProperties minioProperties;
    @Autowired
    private CustomMinioAsyncClient minioAsyncClient;
    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private DocumentService documentService;

    @DubboReference
    private DubboDocUserInfoService dubboDocUserInfoService;
    @DubboReference
    private DubboDocUpdateDataService dubboDocUpdateDataService;


    @Override
    public R<?> minioGetUploadId(MinioGetUploadInfoParam infoParam) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, ExecutionException, InvalidKeyException, InterruptedException, XmlParserException, InvalidResponseException, InternalException {
        // 计算需要分片的数量，向上取整
        double partCount = Math.ceil(infoParam.getFileSize() / infoParam.getChunkSize());
        MinioUploadInfo minioUploadInfo = minioUtil.initMultiPartUpload(infoParam.getFileName(), (int) partCount, infoParam.getContentType());
        return R.success(minioUploadInfo);
    }

    @Override
    public R<?> minioCheckFileByMd5(String md5) {
        return null;
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
                        .object(minioId)
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
                                .object(file.getOriginalFilename())
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

}
