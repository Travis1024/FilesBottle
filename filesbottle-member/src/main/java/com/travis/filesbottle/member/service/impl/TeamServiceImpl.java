package com.travis.filesbottle.member.service.impl;

import com.travis.filesbottle.member.entity.Team;
import com.travis.filesbottle.member.mapper.TeamMapper;
import com.travis.filesbottle.member.service.TeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author travis-wei
 * @since 2023-04-05
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

}
