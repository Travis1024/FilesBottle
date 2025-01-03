package com.travis.filesbottle.minio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.minio.entity.Document;
import com.travis.filesbottle.minio.mapper.DocumentMapper;
import com.travis.filesbottle.minio.service.DocumentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public Document getTeamIdByDocId(String minioId) {
        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Document.DOC_MINIO_ID, minioId);
        return documentMapper.selectOne(queryWrapper);
    }

    @Override
    public List<Document> listAll(String teamId) {
        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Document.DOC_TEAMID, teamId);
        return documentMapper.selectList(queryWrapper);
    }

    @Override
    public Page<Document> selectPage(Page<Document> page, QueryWrapper<Document> queryWrapper) {
        return documentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Document getDocInfoById(String sourceId) {
        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Document.DOC_MINIO_ID, sourceId);
        return documentMapper.selectOne(queryWrapper);
    }

    @Override
    public int deleteMysqlRecord(String sourceId) {
        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Document.DOC_MINIO_ID, sourceId);
        return documentMapper.delete(queryWrapper);
    }
}
