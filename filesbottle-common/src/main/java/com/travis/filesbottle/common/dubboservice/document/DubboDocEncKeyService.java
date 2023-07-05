package com.travis.filesbottle.common.dubboservice.document;

/**
 * @ClassName DubboDocEncKeyService
 * @Description Dubbo Document模块（视频文件密钥模块）
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/5
 */
public interface DubboDocEncKeyService {

    boolean insertDocEncKey(String gridFsId, String encKey);

    String getDocEncKey(String gridFsId);

}
