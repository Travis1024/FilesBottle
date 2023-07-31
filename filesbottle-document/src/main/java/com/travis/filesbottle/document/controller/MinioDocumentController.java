package com.travis.filesbottle.document.controller;

import cn.hutool.core.util.StrUtil;
import com.travis.filesbottle.common.enums.BizCodeEnum;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.entity.bo.MinioGetUploadInfoParam;
import com.travis.filesbottle.document.service.MinioDocumentService;
import com.travis.filesbottle.document.utils.CustomMinioAsyncClient;
import com.travis.filesbottle.document.utils.MinioProperties;
import io.minio.CreateMultipartUploadResponse;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * @ClassName MinioDocumentController
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/6
 */
@Slf4j
@RestController
@RequestMapping("/minio")
@Api("Minio 文件操作 Controller")
public class MinioDocumentController {

    public static final String USER_ID = "userId";
    public static final String USER_NAME = "username";

    @Autowired
    private MinioProperties minioProperties;
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private CustomMinioAsyncClient minioAsyncClient;
    @Autowired
    private MinioDocumentService minioDocumentService;

    @ApiOperation(value = "表单向 minio 上传文件（单个文件，无需分片）")
    @PostMapping("/upload")
    public R<?> minioDocUpload(@RequestParam("file") MultipartFile file, @RequestParam("property") String property, @RequestParam("description") String description, HttpServletRequest request) {
        String userId = request.getHeader(USER_ID);
        String userName = request.getHeader(USER_NAME);
        String originalFilename = file.getOriginalFilename();

        // 检查originalFilename是否为空
        if (StrUtil.isEmpty(originalFilename)) {
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "无法获取文件名，请检查文件！");
        }

        FileDocument fileDocument = null;

        try {
            CompletableFuture<CreateMultipartUploadResponse> multipartUploadAsync = minioAsyncClient.createMultipartUploadAsync("a", "a", "a", null, null);
            String s1 = multipartUploadAsync.get().result().uploadId();
        } catch (Exception e) {

        }
        return R.success("文件上传成功！", fileDocument);
    }

    @ApiOperation(value = "获取上传 url 等参数 (文件分片)")
    @PostMapping("/getUploadInfo")
    public R<?> minioGetUploadId(@RequestBody MinioGetUploadInfoParam infoParam) {
        try {
            return minioDocumentService.minioGetUploadId(infoParam);
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }
    }

    @ApiOperation(value = "通过文件 MD5 校验文件是否存在")
    @GetMapping("/checkFile")
    public R<?> minioCheckFileByMd5(@RequestParam("md5") String md5) {
        R<?> checkFileByMd5Result = minioDocumentService.minioCheckFileByMd5(md5);
        return null;
    }

}
