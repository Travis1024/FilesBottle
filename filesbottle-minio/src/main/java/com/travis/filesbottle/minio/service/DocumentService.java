package com.travis.filesbottle.minio.service;

import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.minio.entity.Document;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author travis-wei
 * @since 2023-07-31
 */
public interface DocumentService extends IService<Document> {

    R<?> searchFileByMd5(String md5, String teamId);

    void insertOne(Document document);

    Document getTeamIdByDocId(String minioId);

    List<Document> listAll();
}
