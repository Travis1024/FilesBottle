package com.travis.filesbottle.member.dubboimpl;


import com.travis.filesbottle.common.dubboservice.member.DubboUserInfoService;
import com.travis.filesbottle.common.dubboservice.member.bo.DubboMemberUser;
import com.travis.filesbottle.member.entity.User;
import com.travis.filesbottle.member.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName DubboMemberUserImpl
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/5
 */
@Slf4j
@DubboService
public class DubboUserInfoServiceImpl implements DubboUserInfoService {

    @Autowired
    private UserMapper userMapper;

    /**
     * @MethodName getUserBasicInfo
     * @Description 根据用户ID查询用户基础信息（密码）
     * @Author travis-wei
     * @Data 2023/4/5
     * @param userId
     * @Return com.travis.filesbottle.common.dubboservice.member.bo.DubboMemberUser
     **/
    @Override
    public DubboMemberUser getUserBasicInfo(String userId) {
        User user = userMapper.getUserBasicInfo(userId);

        DubboMemberUser dubboMemberUser = new DubboMemberUser();
        dubboMemberUser.setUserId(userId);
        dubboMemberUser.setUserPassword(user.getUserPassword());
        dubboMemberUser.setUserName(user.getUserName());
        return dubboMemberUser;
    }
}
