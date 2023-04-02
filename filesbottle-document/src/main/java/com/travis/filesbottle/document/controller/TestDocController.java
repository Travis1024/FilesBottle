package com.travis.filesbottle.document.controller;

import com.travis.filesbottle.common.utils.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName TestDocController
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/2
 */
@RestController
public class TestDocController {

    @PostMapping("/test1")
    public R test1() {
        String result = "document test1";
        return R.success(result);
    }
}
