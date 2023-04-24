package com.travis.filesbottle.common.dubboservice.member;

/**
 * @ClassName DubboDocUpdateDataService
 * @Description Dubbo更新文档数据信息服务接口
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
public interface DubboDocUpdateDataService {
    void updateUserDocNumber(String userId, String property, String number);

    void updateTeamDocNumber(String teamId, String property, String number);
}
