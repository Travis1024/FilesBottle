package com.travis.filesbottle.minio.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.travis.filesbottle.common.enums.BizCodeEnum;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.minio.entity.Document;
import com.travis.filesbottle.minio.entity.bo.MinioGetUploadInfoParam;
import com.travis.filesbottle.minio.service.MinioService;
import com.travis.filesbottle.minio.service.TaskExecuteService;
import com.travis.filesbottle.minio.utils.CustomMinioAsyncClient;
import com.travis.filesbottle.minio.utils.MinioProperties;
import io.minio.*;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.XmlParserException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author travis-wei
 * @since 2023-07-31
 */
@Slf4j
@RestController
@RequestMapping("/minio")
@Api("Minio 文件操作 Controller")
public class MinioController {
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "username";

    @Autowired
    private MinioService minioService;
    @Autowired
    private TaskExecuteService taskExecuteService;

    @ApiOperation(value = "表单向 minio 上传文件（单个文件，无需分片）")
    @PostMapping("/uploadSingle")
    public R<?> minioDocUpload(@RequestParam("file") MultipartFile file, @RequestParam("property") String property, @RequestParam("description") String description, HttpServletRequest request) {
        String userId = request.getHeader(USER_ID);
        String userName = request.getHeader(USER_NAME);

        Document document;

        try {
            // 异步上传单个文件
            R<?> result = minioService.uploadSingleDoc(userId, userName, property, description, file);
            // 获取上传结果及文件信息
            if (!R.checkSuccess(result)) return result;
            document = (Document) result.getData();
            // 异步执行生成预览文件、更新 mysql 数据、更新 ElasticSearch 数据的任务
            taskExecuteService.generatePreviewFile(document, file);

        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }

        return R.success("文件上传成功！", document);
    }

    @ApiOperation(value = "获取上传 url 等参数 (文件分片)")
    @PostMapping("/getUploadInfo")
    public R<?> minioGetUploadId(@RequestBody MinioGetUploadInfoParam infoParam) {
        try {
            return minioService.minioGetUploadId(infoParam);
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }
    }

    @ApiOperation(value = "通过文件 MD5 校验文件是否存在")
    @GetMapping("/checkFile")
    public R<?> minioCheckFileByMd5(@RequestParam("md5") String md5) {
        R<?> checkFileByMd5Result = minioService.minioCheckFileByMd5(md5);
        return null;
    }

}
