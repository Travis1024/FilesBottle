package com.travis.filesbottle.document.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travis.filesbottle.common.constant.PageConstants;
import com.travis.filesbottle.common.enums.BizCodeEnum;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.enums.FileTypeEnum;
import com.travis.filesbottle.document.service.DocumentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PostMapping("/upload")
    public R<?> documentUpload(@RequestParam("file") MultipartFile file, @RequestParam("property") String property, @RequestParam("description") String description, ServerHttpRequest request) {
        String userId = request.getHeaders().getFirst(USER_ID);
        String userName = request.getHeaders().getFirst(USER_NAME);
        String originalFilename = file.getOriginalFilename();

        // 检查originalFilename是否为空
        if (StrUtil.isEmpty(originalFilename)) {
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "无法获取文件名，请检查文件！");
        }

        try {

            String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            // 判断文件类型是否支持预览
            boolean judgeSupportType = FileTypeEnum.judgeSupportType(suffix);

            // 获取文件的md5值
            String fileMd5 = SecureUtil.md5(file.getInputStream());

            // 上传文件 TODO 判断业务状态码
            documentService.uploadFile(userId, userName, fileMd5, property, description, file);

            if (!fileDocument.getDocUserid().equals(userId)) {
                // TODO 团队文件中已经存在该文档
            }

            if (judgeSupportType) {
                // TODO 执行生成预览文件的操作
                if (suffix.equals(FileTypeEnum.PDF.getFileType())) {

                } else if (suffix.equals(FileTypeEnum.XLS.getFileType())) {

                } else if (suffix.equals(FileTypeEnum.XLSX.getFileType())) {

                } else if (suffix.equals(FileTypeEnum.DOC.getFileType())) {

                } else if (suffix.equals(FileTypeEnum.DOCX.getFileType())) {

                } else if (suffix.equals(FileTypeEnum.PPT.getFileType())) {

                } else if (suffix.equals(FileTypeEnum.PPTX.getFileType())) {

                }
            }



        } catch (IOException exception) {
            log.error(exception.getMessage());
        }

    }

}
