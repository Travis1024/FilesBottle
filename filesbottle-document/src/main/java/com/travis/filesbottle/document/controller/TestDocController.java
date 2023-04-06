package com.travis.filesbottle.document.controller;

import com.travis.filesbottle.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName TestDocController
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/2
 */

@Api("文档模块测试Controller")
@RestController
public class TestDocController {

    @ApiOperation(value = "测试接口1")
    @GetMapping("/test1")
    public R test1() {
        String result = "document test1";
        return R.success(result);
    }
}
