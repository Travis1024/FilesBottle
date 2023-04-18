package com.travis.filesbottle.member.dubboimpl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.travis.filesbottle.common.constant.DocumentConstants;
import com.travis.filesbottle.common.dubboservice.document.DubboDocUpdateDataService;
import com.travis.filesbottle.member.entity.Team;
import com.travis.filesbottle.member.entity.User;
import com.travis.filesbottle.member.mapper.TeamMapper;
import com.travis.filesbottle.member.mapper.UserMapper;
import com.travis.filesbottle.member.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName DubboDocUpdateDataServiceImpl
 * @Description Dubbo更新文档数据信息服务实现类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/12
 */
@Slf4j
@DubboService
public class DubboDocUpdateDataServiceImpl implements DubboDocUpdateDataService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TeamMapper teamMapper;

    /**
     * @MethodName updateUserDocNumber
     * @Description 更新个人上传的文档数量
     * @Author travis-wei
     * @Data 2023/4/12
     * @param userId
     * @param property
     * @param number
     * @Return void
     **/
    @Override
    public void updateUserDocNumber(String userId, String property, String number) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(User.USER_ID, userId);
        String sql = "{} = {} + {}";

        // 需根据文档属性进行数据更新
        if (DocumentConstants.DOC_PUBLIC.equals(property)) {
            sql = StrUtil.format(sql, User.USER_DOC_PUBLIC_NUMBER, User.USER_DOC_PUBLIC_NUMBER, number);
        } else if (DocumentConstants.DOC_PRIVATE.equals(property)) {
            sql = StrUtil.format(sql, User.USER_DOC_PRIVATE_NUMBER, User.USER_DOC_PRIVATE_NUMBER, number);
        } else {
            log.error("文档属性错误！");
            throw new RuntimeException("文档属性错误！");
        }
        updateWrapper.setSql(sql);
        userMapper.update(null, updateWrapper);
    }

    /**
     * @MethodName updateTeamDocNumber
     * @Description 更新团队文档数量
     * @Author travis-wei
     * @Data 2023/4/12
     * @param teamId
     * @param property
     * @param number
     * @Return void
     **/
    @Override
    public void updateTeamDocNumber(String teamId, String property, String number) {
        UpdateWrapper<Team> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(Team.TEAM_ID, teamId);
        String sql = "{} = {} + {}";

        if (DocumentConstants.DOC_PUBLIC.equals(property)) {
            sql = StrUtil.format(sql, Team.TEAM_DOC_PUBLIC_NUMBER, Team.TEAM_DOC_PUBLIC_NUMBER, number);
        } else if (DocumentConstants.DOC_PRIVATE.equals(property)) {
            sql = StrUtil.format(sql, Team.TEAM_DOC_PRIVATE_NUMBER, Team.TEAM_DOC_PRIVATE_NUMBER, number);
        } else {
            log.error("文档属性错误！");
            throw new RuntimeException("文档属性错误！");
        }
        updateWrapper.setSql(sql);
        teamMapper.update(null, updateWrapper);
    }
}
