package com.travis.filesbottle.document.dubboimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.travis.filesbottle.common.dubboservice.document.DubboDocEncKeyService;
import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.mapper.DocumentMapper;
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
    public boolean insertDocEncKey(String gridFsId, String encKey) {
        UpdateWrapper<FileDocument> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(FileDocument.DOC_GRIDFS_ID, gridFsId);
        updateWrapper.set(FileDocument.DOC_ENC_KEY, encKey);
        int update = documentMapper.update(null, updateWrapper);
        return update > 0;
    }

    @Override
    public String getDocEncKey(String gridFsId) {
        QueryWrapper<FileDocument> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(FileDocument.DOC_GRIDFS_ID, gridFsId);
        FileDocument fileDocument = documentMapper.selectOne(queryWrapper);
        if (fileDocument == null) return null;
        return fileDocument.getDocEncKey();
    }
}
