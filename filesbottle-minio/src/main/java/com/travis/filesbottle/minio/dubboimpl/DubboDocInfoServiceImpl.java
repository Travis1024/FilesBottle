package com.travis.filesbottle.minio.dubboimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.travis.filesbottle.common.dubboservice.document.DubboDocInfoService;
import com.travis.filesbottle.common.dubboservice.member.DubboUserInfoService;
import com.travis.filesbottle.minio.entity.Document;
import com.travis.filesbottle.minio.mapper.DocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName DubboDocInfoServiceImpl
 * @Description TODO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/8/3
 */
@Slf4j
@DubboService
@Service
public class DubboDocInfoServiceImpl implements DubboDocInfoService {

    @Autowired
    private DocumentMapper documentMapper;

    @Override
    public String getTeamIdByDocId(String docId) {
        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(Document.DOC_MINIO_ID, docId);
        Document document = documentMapper.selectOne(queryWrapper);
        if (document != null) {
            return document.getDocTeamid();
        }
        return null;
    }
}
