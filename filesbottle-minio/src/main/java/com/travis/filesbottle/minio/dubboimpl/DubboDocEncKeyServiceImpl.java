package com.travis.filesbottle.minio.dubboimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.travis.filesbottle.common.dubboservice.document.DubboDocEncKeyService;
import com.travis.filesbottle.minio.entity.Document;
import com.travis.filesbottle.minio.mapper.DocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName DubboDocEncKeyServiceImpl
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/7/5
 */
@Slf4j
@DubboService
@Service
public class DubboDocEncKeyServiceImpl implements DubboDocEncKeyService {

    @Autowired
    private DocumentMapper documentMapper;


    @Override
    public boolean insertDocEncKey(String minioId, String encKey) {
        UpdateWrapper<Document> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(Document.DOC_MINIO_ID, minioId);
        updateWrapper.set(Document.DOC_ENC_KEY, encKey);
        int update = documentMapper.update(null, updateWrapper);
        return update > 0;
    }

    @Override
    public String getDocEncKey(String minioId) {
        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Document.DOC_MINIO_ID, minioId);
        Document document = documentMapper.selectOne(queryWrapper);
        if (document == null) return null;
        return document.getDocEncKey();
    }
}
