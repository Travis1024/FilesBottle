package com.travis.filesbottle.member.service.impl;

import com.travis.filesbottle.member.entity.User;
import com.travis.filesbottle.member.mapper.UserMapper;
import com.travis.filesbottle.member.service.UserService;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
