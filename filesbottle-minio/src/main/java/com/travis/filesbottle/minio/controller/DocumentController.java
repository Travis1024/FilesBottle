package com.travis.filesbottle.minio.controller;

import com.travis.filesbottle.common.enums.BizCodeEnum;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.minio.service.DocumentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author travis-wei
 * @since 2023-07-31
 */
@Slf4j
@Api(value = "Minio文件Controller模块")
@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @ApiOperation(value = "根据md5搜索团队文件中的文件信息")
    @GetMapping("/searchFileByMd5")
    public R<?> searchFileByMd5(@RequestParam("md5") String md5, @RequestParam("teamId") String teamId) {
        try {
            return documentService.searchFileByMd5(md5, teamId);
        } catch (Exception e) {
            log.error(e.toString());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }
    }
}
