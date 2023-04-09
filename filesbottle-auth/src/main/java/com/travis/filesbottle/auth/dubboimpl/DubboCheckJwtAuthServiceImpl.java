package com.travis.filesbottle.auth.dubboimpl;

import cn.hutool.core.util.StrUtil;
import com.travis.filesbottle.auth.config.JwtPropertiesConfiguration;
import com.travis.filesbottle.auth.utils.JwtTokenUtil;
import com.travis.filesbottle.common.dubboservice.auth.DubboCheckJwtAuthService;
import com.travis.filesbottle.common.dubboservice.auth.bo.DubboAuthUser;
import com.travis.filesbottle.common.enums.BizCodeEnum;
import com.travis.filesbottle.common.utils.R;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.Map;

/**
 * @ClassName DubboCheckJwtAuthServiceImpl
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/7
 */
@DubboService
public class DubboCheckJwtAuthServiceImpl implements DubboCheckJwtAuthService {
    @Override
    public R<DubboAuthUser> checkJwtAuth(String token) {
        // 验证 token 里面的 userId 是否为空
        String userId = JwtTokenUtil.getUserIdFromToken(token);
        String username = JwtTokenUtil.getUserNameFromToken(token);
        if (StrUtil.isEmpty(userId)) {
            return R.error(BizCodeEnum.MOUDLE_AUTH, BizCodeEnum.TOKEN_CHECK_FAILED);
        }

        DubboAuthUser dubboAuthUser = new DubboAuthUser();
        dubboAuthUser.setUserId(userId);
        dubboAuthUser.setUserName(username);

        // 对Token解签名，并验证Token是否过期
        boolean isJwtNotValid = JwtTokenUtil.isTokenExpired(token);

        // 如果token已经过期，判断token能够被刷新
        if(isJwtNotValid){
            if (JwtTokenUtil.isTokenCouldRefresh(token)) {
                Map<String, Object> newTokenMap = null;
                if (JwtTokenUtil.isOnceRefresh()) {
                    newTokenMap = JwtTokenUtil.onceRefreshToken(token);
                    dubboAuthUser.setNewTokenMap(newTokenMap);
                } else {
                    newTokenMap = JwtTokenUtil.generateTokenAndRefreshToken(userId, username);
                    dubboAuthUser.setNewTokenMap(newTokenMap);
                }

                // 前端需要判断该响应码，更新请求头中的token信息。如果前端没有更新token，之后继续使用旧的token发送请求，则会提示token已过期
                return R.error(BizCodeEnum.MOUDLE_AUTH, BizCodeEnum.TOKEN_REFRESH, dubboAuthUser);
            }
            return R.error(BizCodeEnum.MOUDLE_AUTH, BizCodeEnum.TOKEN_EXPIRED);
        }

        return R.success("token鉴权成功！", dubboAuthUser);
    }
}
