package com.travis.filesbottle.common.dubboservice.member;

import com.travis.filesbottle.common.dubboservice.member.bo.DubboMemberUser;

/**
 * @ClassName UserInfoService
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/5
 */
public interface DubboUserInfoService {
    /**
     * @MethodName getUserBasicInfo
     * @Description 根据用户id查询用户密码
     * @Author travis-wei
     * @Data 2023/4/5
     * @param userId
     * @Return com.travis.filesbottle.common.dubboservice.member.bo.DubboMemberUser
     **/
    public DubboMemberUser getUserBasicInfo(String userId);
}
