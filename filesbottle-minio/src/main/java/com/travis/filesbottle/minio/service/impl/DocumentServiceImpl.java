package com.travis.filesbottle.minio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.minio.entity.Document;
import com.travis.filesbottle.minio.mapper.DocumentMapper;
import com.travis.filesbottle.minio.service.DocumentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author travis-wei
 * @since 2023-07-31
 */
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Autowired
    private DocumentMapper documentMapper;

    @Override
    public R<?> searchFileByMd5(String md5, String teamId) {
        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Document.DOC_MD5, md5).eq(Document.DOC_TEAMID, teamId);
        Document document = documentMapper.selectOne(queryWrapper);
        return R.success(document);
    }

    @Override
    public void insertOne(Document document) {
        documentMapper.insert(document);
    }
}
