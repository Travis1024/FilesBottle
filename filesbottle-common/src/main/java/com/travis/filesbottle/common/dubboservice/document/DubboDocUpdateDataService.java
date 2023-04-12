package com.travis.filesbottle.common.dubboservice.document;

/**
 * @ClassName DubboDocUpdateDataService
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
public interface DubboDocUpdateDataService {
    void updateUserDocNumber(String userId, String property, String number);

    void updateTeamDocNumber(String teamId, String property, String number);
}
