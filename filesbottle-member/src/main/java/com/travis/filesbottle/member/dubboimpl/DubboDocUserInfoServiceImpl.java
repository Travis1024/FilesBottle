package com.travis.filesbottle.member.dubboimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.travis.filesbottle.common.dubboservice.member.DubboDocUserInfoService;
import com.travis.filesbottle.common.dubboservice.member.bo.DubboDocumentUser;
import com.travis.filesbottle.member.entity.User;
import com.travis.filesbottle.member.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName DubboDocUserInfoServiceImpl
 * @Description Dubbo查询用户基本信息服务实现类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/11
 */
@DubboService
@Slf4j
@Service
public class DubboDocUserInfoServiceImpl implements DubboDocUserInfoService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public DubboDocumentUser getDocumentUserInfo(String userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(User.USER_ID, userId);
        User user = userMapper.selectOne(queryWrapper);
        DubboDocumentUser documentUser = new DubboDocumentUser();
        if (user == null) {
            log.warn("方法：getDocumentUserInfo，用户不存在");
            return documentUser;
        }
        documentUser.setUserId(user.getUserId());
        documentUser.setUserTeamId(user.getUserTeam());
        documentUser.setUserBanning(user.getUserBanning());

        return documentUser;
    }
}
