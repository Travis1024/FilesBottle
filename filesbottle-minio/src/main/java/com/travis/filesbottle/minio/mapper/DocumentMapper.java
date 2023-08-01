package com.travis.filesbottle.minio.mapper;

import com.travis.filesbottle.minio.entity.Document;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author travis-wei
 * @since 2023-07-31
 */
@Mapper
public interface DocumentMapper extends BaseMapper<Document> {

}