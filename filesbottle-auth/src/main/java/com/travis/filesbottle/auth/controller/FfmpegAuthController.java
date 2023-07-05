package com.travis.filesbottle.auth.controller;

import cn.hutool.core.util.StrUtil;
import com.travis.filesbottle.auth.config.JwtPropertiesConfiguration;
import com.travis.filesbottle.auth.utils.JwtTokenUtil;
import com.travis.filesbottle.common.dubboservice.document.DubboDocEncKeyService;
import com.travis.filesbottle.common.enums.BizCodeEnum;
import com.travis.filesbottle.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName FfmpegAuthController
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/5
 */
@Slf4j
@Api(tags = "视频文件鉴权Controller")
@RestController
@RequestMapping("/ffmpeg")
public class FfmpegAuthController {

    private static final String keyPrefix = "jwt:hlsToken:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @DubboReference
    private DubboDocEncKeyService dubboDocEncKeyService;

    @ApiOperation(value = "获取视频文件加密的 key")
    @GetMapping("/getkey")
    public R<?> getVideoEncKey(@RequestParam("gridFsId") String gridFsId, @RequestParam("token") String token, HttpServletRequest request) {

        // 判断 redis 是否含有该 token
        if (Boolean.FALSE.equals(redisTemplate.boundSetOps(keyPrefix + gridFsId).isMember(token))) {
            return R.error(BizCodeEnum.TOKEN_CHECK_FAILED, "视频token已过期！");
        } else {
            // 移除 token
            redisTemplate.boundSetOps(keyPrefix + gridFsId).remove(token);
        }

        // 验证 token
        String userIdFromToken = JwtTokenUtil.getUserIdFromToken(token);
        if (StrUtil.isEmpty(userIdFromToken) || !userIdFromToken.equals(request.getHeader("userId"))) return R.error(BizCodeEnum.TOKEN_CHECK_FAILED);

        if (JwtTokenUtil.isTokenExpired(token)) {
            return R.error(BizCodeEnum.TOKEN_CHECK_FAILED, "视频token已过期！");
        }

        // 查询文件 key，并返回
        String docEncKey = dubboDocEncKeyService.getDocEncKey(gridFsId);
        if (StrUtil.isEmpty(docEncKey)) return R.error(BizCodeEnum.UNKNOW, "视频key获取失败！");
        return R.success(docEncKey);
    }

}
