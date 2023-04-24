package com.travis.filesbottle.common.dubboservice.member;

import com.travis.filesbottle.common.dubboservice.member.bo.DubboDocumentUser;

/**
 * @ClassName DubboDocUserInfoService
 * @Description Dubbo查询用户基本信息服务接口
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/11
 */
public interface DubboDocUserInfoService {

    /**
     * @MethodName getDocumentUserInfo
     * @Description 根据用户ID查询用户基本信息，提供给文档模块使用
     * @Author travis-wei
     * @Data 2023/4/11
     * @param userId
     * @Return com.travis.filesbottle.common.dubboservice.document.bo.DubboDocumentUser
     **/
    DubboDocumentUser getDocumentUserInfo(String userId);
}
