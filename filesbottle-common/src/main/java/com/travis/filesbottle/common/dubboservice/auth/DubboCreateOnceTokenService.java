package com.travis.filesbottle.common.dubboservice.auth;

import com.travis.filesbottle.common.utils.R;

/**
 * @ClassName DubboAuthCreateOnceToken
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/5
 */
public interface DubboCreateOnceTokenService {

    R<?> createOnceToken(String gridFsId, String userId);

}
