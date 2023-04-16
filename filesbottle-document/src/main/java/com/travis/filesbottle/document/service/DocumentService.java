package com.travis.filesbottle.document.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.document.entity.FileDocument;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travis.filesbottle.document.entity.dto.DownloadDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author travis-wei
 * @since 2023-04-11
 */
public interface DocumentService extends IService<FileDocument> {

    List<FileDocument> selectAllListByPage(Page<FileDocument> page, QueryWrapper<FileDocument> queryWrapper);

    R<?> uploadFile(String userId, String userName, String fileMd5, String property, String description, MultipartFile file);

    FileDocument searchFileByMd5(String md5, String teamId);

    R<?> getPreviewDocStream(String sourceId);

    R<?> getSourceDocStream(String sourceId);
}
