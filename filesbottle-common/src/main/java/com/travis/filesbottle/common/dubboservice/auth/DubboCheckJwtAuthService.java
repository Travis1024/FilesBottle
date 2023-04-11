package com.travis.filesbottle.common.dubboservice.auth;

import com.travis.filesbottle.common.dubboservice.auth.bo.DubboAuthUser;
import com.travis.filesbottle.common.utils.R;

/**
 * @ClassName DubboCheckJwtAuthService
 * @Description Dubbo-用户鉴权服务接口
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/7
 */
public interface DubboCheckJwtAuthService {

    R<DubboAuthUser> checkJwtAuth(String token);

}
