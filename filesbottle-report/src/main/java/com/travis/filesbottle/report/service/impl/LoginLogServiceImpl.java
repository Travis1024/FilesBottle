package com.travis.filesbottle.report.service.impl;

import com.travis.filesbottle.report.entity.LoginLog;
import com.travis.filesbottle.report.mapper.LoginLogMapper;
import com.travis.filesbottle.report.service.LoginLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author travis-wei
 * @since 2023-04-27
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

}
