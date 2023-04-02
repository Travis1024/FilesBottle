package com.travis.filesbottle.auth.controller;

import com.travis.filesbottle.common.utils.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName TestAuthController
 * @Description Auth Controller测试类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/2
 */
@RestController
public class TestAuthController {

    @PostMapping("/test1")
    public R test1() {
        String result = "测试成功-test1";
        return R.success(result);
    }
}
