package com.travis.filesbottle.member.mapper;

import com.travis.filesbottle.member.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author travis-wei
 * @since 2023-04-05
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
