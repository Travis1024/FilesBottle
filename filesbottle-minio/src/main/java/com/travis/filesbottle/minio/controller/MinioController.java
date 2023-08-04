package com.travis.filesbottle.minio.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travis.filesbottle.common.constant.Constants;
import com.travis.filesbottle.common.constant.PageConstants;
import com.travis.filesbottle.common.enums.BizCodeEnum;
import com.travis.filesbottle.common.utils.BizCodeUtil;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.minio.entity.Document;
import com.travis.filesbottle.minio.entity.bo.MinioGetUploadInfoParam;
import com.travis.filesbottle.minio.entity.bo.MinioMergeParam;
import com.travis.filesbottle.minio.entity.bo.MinioUploadInfo;
import com.travis.filesbottle.minio.service.MinioService;
import com.travis.filesbottle.minio.service.TaskExecuteService;
import com.travis.filesbottle.minio.utils.CustomMinioAsyncClient;
import com.travis.filesbottle.minio.utils.MinioProperties;
import com.travis.filesbottle.minio.utils.MinioUtil;
import io.minio.*;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.XmlParserException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
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

    @ApiOperation(value = "根据page查询文件列表 (当前团队所属文档)")
    @GetMapping("/list/{pageSize}/{pageCurrent}")
    public R<?> list(@PathVariable Integer pageSize, @PathVariable Integer pageCurrent) {
        // 如果page size不是10、20、50, 默认更改为10
        if (!pageSize.equals(PageConstants.PAGESIZE10) && !pageSize.equals(PageConstants.PAGESIZE20) && !pageSize.equals(PageConstants.PAGESIZE50)) {
            pageSize = PageConstants.PAGESIZE10;
        }

        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
        Page<Document> page = new Page<>(pageCurrent, pageSize);

        List<Document> documentList;
        try {
            documentList = minioService.selectAllListByPage(page, queryWrapper);
        } catch (Exception e) {
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, "分页查询失败!");
        }
        return R.success(documentList);
    }

    @ApiOperation(value = "查询当前团队中的文件列表")
    @GetMapping("/listAll")
    public R<?> listAll(HttpServletRequest request) {
        String userId = request.getHeader(USER_ID);
        String userName = request.getHeader(USER_NAME);

        try {
            return minioService.listAll(userId);
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }
    }

    @ApiOperation(value = "根据关键词ElasticSearch文档")
    @GetMapping("/search")
    public R<?> esDocumentByKeyword(@RequestParam("keyword") String keyword, HttpServletRequest request) {
        // 从请求头中获取用户 ID 信息
        String userId = request.getHeader(USER_ID);
        try {
            return minioService.esDocumentByKeyword(keyword, userId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }
    }

    @ApiOperation(value = "获取预览文件，maybe 不支持在线预览｜pdf在线预览｜源文件在线预览｜kkFileView的url在线预览")
    @GetMapping("/stream/preview")
    public R<?> getPreviewStream(@RequestParam("sourceId") String sourceId, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        try {
            String userId = request.getHeader(USER_ID);
            return minioService.getPreviewStream(sourceId, userId, response);
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }
    }

    @ApiOperation(value = "文件删除接口，分三种情况：支持转为pdf进行在线预览 ｜ 支持使用kkFileView在线预览 ｜ 其他（不支持在线预览、源文件流即可预览、未知类型）")
    @DeleteMapping("/delete/document")
    public R<?> deleteDocumentById(@RequestParam("sourceId") String sourceId, HttpServletRequest request) {
        try {
            String userId = request.getHeader(USER_ID);
            return minioService.deleteDocumentById(sourceId, userId);
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }
    }

    @ApiOperation(value = "根据源文件ID下载源文件")
    @GetMapping("/download/source")
    public ResponseEntity<Object> downloadSourceDocument(@RequestParam("objectName") String objectName, @RequestParam("fileName") String fileName, HttpServletRequest request) throws UnsupportedEncodingException {
        try {
            String userId = request.getHeader(USER_ID);
            return minioService.downloadSourceDocument(objectName, fileName, userId);
        } catch (Exception e) {
            log.error(e.toString());
            return null;
        }
    }

    @ApiOperation(value = "表单向 minio 上传文件（单个文件，无需分片）")
    @PostMapping("/uploadSingle")
    public R<?> minioDocUpload(@RequestParam("file") MultipartFile file, @RequestParam("property") String property, @RequestParam("description") String description, HttpServletRequest request) {
        String userId = request.getHeader(USER_ID);
        String userName = request.getHeader(USER_NAME);

        Document document;

        try {
            // 上传单个文件
            R<?> result = minioService.uploadSingleDoc(userId, userName, property, description, file);
            // 获取上传结果及文件信息
            if (!R.checkSuccess(result)) return result;
            document = (Document) result.getData();
            // 异步执行生成预览文件、更新 mysql 数据、更新 ElasticSearch 数据的任务
            taskExecuteService.generatePreviewFile(file.getSize(), document, file.getInputStream());

        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }

        return R.success("文件上传成功！", document);
    }

    @ApiOperation(value = "获取上传 url 等参数 (文件分片)")
    @PostMapping("/getUploadInfo")
    public R<?> minioGetUploadId(@RequestBody MinioGetUploadInfoParam infoParam, HttpServletRequest request) {

        String userId = request.getHeader(USER_ID);
        String userName = request.getHeader(USER_NAME);

        try {
            return minioService.minioGetUploadId(userId, userName, infoParam);
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }
    }

    @ApiOperation(value = "文件分片合并")
    @PostMapping("/merge")
    public R<?> mergeUploadParts(@RequestBody MinioMergeParam minioMergeParam, HttpServletRequest request) {

        String userId = request.getHeader(USER_ID);
        String userName = request.getHeader(USER_NAME);

        R<Document> merged;
        try {
            // 分片文件合并
            merged = minioService.mergeUploadParts(userId, userName, minioMergeParam);
            if (!R.checkSuccess(merged)) return merged;

            // 合并成功后：异步执行生成预览文件、更新 mysql 数据、更新 ElasticSearch 数据的任务
            taskExecuteService.generatePreviewFile(minioMergeParam.getMinioGetUploadInfoParam().getFileSize(), merged.getData(), null);

        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }

        return R.success("文件上传成功！", merged.getData());
    }

    @ApiOperation(value = "获取已上传的文件切片列表")
    @GetMapping("/uploadChunkList")
    public R<?> listUploadChunkList(@RequestParam("objectName") String objectName, @RequestParam("uploadId") String uploadId) {
        try {
            return minioService.listUploadChunkList(objectName, uploadId);
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }
    }



}
