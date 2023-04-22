package com.travis.filesbottle.document.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mongodb.client.gridfs.GridFSBucket;
import com.travis.filesbottle.common.constant.Constants;
import com.travis.filesbottle.common.constant.PageConstants;
import com.travis.filesbottle.common.enums.BizCodeEnum;
import com.travis.filesbottle.common.utils.BizCodeUtil;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.entity.dto.DownloadDocument;
import com.travis.filesbottle.document.service.DocumentService;
import com.travis.filesbottle.document.service.TaskExecuteService;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @ClassName DocumentsController
 * @Description 文档操作类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/10
 */
@RestController
@Slf4j
@RequestMapping("/operate")
@Api("文档操作Controller")
public class DocumentsController {

    public static final String USER_ID = "userId";
    public static final String USER_NAME = "username";

    @Autowired
    private DocumentService documentService;

    @Autowired
    private TaskExecuteService taskExecuteService;



    @ApiOperation("获取文档总数")
    @GetMapping("/totalNumber")
    public R<?> getTotalDataNumber() {
        return R.success(documentService.count());
    }


    @ApiOperation(value = "根据page查询文件列表")
    @GetMapping("/list/{pageSize}/{pageCurrent}")
    public R<?> list(@PathVariable Integer pageSize, @PathVariable Integer pageCurrent) {

        // 如果page size不是10、20、50, 默认更改为10
        if (!pageSize.equals(PageConstants.PAGESIZE10) && !pageSize.equals(PageConstants.PAGESIZE20) && !pageSize.equals(PageConstants.PAGESIZE50)) {
            pageSize = PageConstants.PAGESIZE10;
        }

        QueryWrapper<FileDocument> queryWrapper = new QueryWrapper<>();
        Page<FileDocument> page = new Page<>(pageCurrent, pageSize);

        List<FileDocument> documentList;
        try {
            documentList = documentService.selectAllListByPage(page, queryWrapper);
        } catch (Exception e) {
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, "分表查询失败!");
        }
        return R.success(documentList);
    }

    @ApiOperation(value = "根据关键词ElasticSearch文档")
    @GetMapping("/search")
    public R<?> esDocumentByKeyword(@RequestParam("keyword") String keyword, HttpServletRequest request) {
        // 从请求头中获取用户id信息
        String userId = request.getHeader(USER_ID);
        List<SearchHit> searchHits;
        try {
            searchHits = documentService.esDocumentByKeyword(keyword, userId);
        } catch (IOException e) {
            log.error(e.getMessage());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }
        return R.success(searchHits);
    }


    @ApiOperation(value = "获取预览文件，maybe 不支持在线预览｜pdf在线预览｜源文件在线预览｜kkFileView的url在线预览")
    @GetMapping("/stream/preview")
    public R<?> getPreviewStream(@RequestParam("sourceId") String sourceId) throws UnsupportedEncodingException {
        R<?> previewDocStream = documentService.getPreviewDocStream(sourceId);
        if (!BizCodeUtil.isCodeSuccess(previewDocStream.getCode())) {
            // 返回失败的响应结果
            return previewDocStream;
        }
        DownloadDocument data = (DownloadDocument) previewDocStream.getData();
        return R.success("预览文件获取成功！", data);
    }



    @ApiOperation(value = "文件删除接口，分三种情况：支持转为pdf进行在线预览 ｜ 支持使用kkFileView在线预览 ｜ 其他（不支持在线预览、源文件流即可预览、未知类型）")
    @DeleteMapping("/delete/document")
    public R<?> deleteDocumentById(@RequestParam("sourceId") String sourceId) {
        return documentService.deleteDocumentById(sourceId);
    }


    @ApiOperation(value = "根据源文件ID下载源文件")
    @GetMapping("/download/source")
    public ResponseEntity<Object> downloadSourceDocument(@RequestParam("sourceId") String sourceId) throws UnsupportedEncodingException {
        // 获取源文件流
        R<?> sourceDocStream = documentService.getSourceDocStream(sourceId);
        if (!BizCodeUtil.isCodeSuccess(sourceDocStream.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sourceDocStream);
        }
        DownloadDocument data = (DownloadDocument) sourceDocStream.getData();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName=" + URLEncoder.encode(data.getDocName(), Constants.UTF8))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data.getBytes());
    }


    @ApiOperation(value = "表单上传文件")
    @PostMapping("/upload")
    public R<?> documentUpload(@RequestParam("file") MultipartFile file, @RequestParam("property") String property, @RequestParam("description") String description, HttpServletRequest request) {
        String userId = request.getHeader(USER_ID);
        String userName = request.getHeader(USER_NAME);
        String originalFilename = file.getOriginalFilename();

        // 检查originalFilename是否为空
        if (StrUtil.isEmpty(originalFilename)) {
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "无法获取文件名，请检查文件！");
        }

        FileDocument fileDocument;

        try {
            // 获取文件的md5值
            String fileMd5 = SecureUtil.md5(file.getInputStream());
            // 上传文件业务
            R<?> result = documentService.uploadFile(userId, userName, fileMd5, property, description, file);
            // 如果业务状态码为失败状态
            if (!BizCodeUtil.isCodeSuccess(result.getCode())) {
                return result;
            }
            fileDocument = (FileDocument) result.getData();
            // 文件记录中的创建者id和请求头的id不同，表明团队文件中已经存在该文档
            if (!fileDocument.getDocUserid().equals(userId)) {
                return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "团队文件库中已存在该文件！");
            }
            // 异步执行生成预览文件、更新mysql数据、更新elasticSearch数据的任务
            taskExecuteService.generatePreviewFile(fileDocument, file.getInputStream(), file);

        } catch (IOException exception) {
            log.error(exception.getMessage());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, exception.getMessage());
        }
        return R.success("文件上传成功！", fileDocument);
    }

}
