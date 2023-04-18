package com.travis.filesbottle.document.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.travis.filesbottle.common.dubboservice.document.DubboDocUpdateDataService;
import com.travis.filesbottle.common.dubboservice.document.DubboDocUserInfoService;
import com.travis.filesbottle.common.dubboservice.document.bo.DubboDocumentUser;
import com.travis.filesbottle.common.enums.BizCodeEnum;
import com.travis.filesbottle.common.utils.BizCodeUtil;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.entity.dto.DownloadDocument;
import com.travis.filesbottle.document.mapper.DocumentMapper;
import com.travis.filesbottle.document.service.DocumentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travis.filesbottle.document.utils.FileTypeEnumUtil;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author travis-wei
 * @since 2023-04-11
 */
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, FileDocument> implements DocumentService {

    private static final String FILE_NAME = "filename";

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @DubboReference
    private DubboDocUserInfoService dubboDocUserInfoService;
    @DubboReference
    private DubboDocUpdateDataService dubboDocUpdateDataService;


    /**
     * @MethodName selectAllListByPage
     * @Description 根据页码返回分页查询的结果
     * @Author travis-wei
     * @Data 2023/4/11
     * @param page
     * @param queryWrapper
     * @Return java.util.List<com.travis.filesbottle.document.entity.FileDocument>
     **/
    @Override
    public List<FileDocument> selectAllListByPage(Page<FileDocument> page, QueryWrapper<FileDocument> queryWrapper) {
        Page<FileDocument> documentPage = documentMapper.selectPage(page, queryWrapper);
        return documentPage.getRecords();
    }

    /**
     * @MethodName uploadFile
     * @Description 上传新文件
     * @Author travis-wei
     * @Data 2023/4/11
     * @param userId
     * @param userName
     * @param fileMd5
     * @param file
     * @Return com.travis.filesbottle.document.entity.FileDocument
     **/
    @Override
    public R<?> uploadFile(String userId, String userName, String fileMd5, String property, String description, MultipartFile file) {

        DubboDocumentUser userInfo = dubboDocUserInfoService.getDocumentUserInfo(userId);

        if (userInfo == null) return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "该用户信息不存在！");
        if (userInfo.getUserBanning() == 1) return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.FORBIDDEN, "该用户已被封禁，无文件上传权限，请联系管理员！");

        FileDocument fileDocument = searchFileByMd5(fileMd5, userInfo.getUserTeamId());

        if (fileDocument != null) {
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "团队中已经存在该文档！");
        }

        String originalFilename = file.getOriginalFilename();
        // 获取文件名的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        fileDocument = new FileDocument();

        fileDocument.setDocName(originalFilename);
        // 计算文件大小，单位为MB (1024 * 1024 = 1048576)
        double docSize = file.getSize() / 1048576.0;
        fileDocument.setDocSize(docSize);
        fileDocument.setDocUploadDate(new Timestamp(new Date().getTime()));
        fileDocument.setDocMd5(fileMd5);
        // 根据文件后缀获取文件的类型码
        fileDocument.setDocFileTypeCode(FileTypeEnumUtil.getCodeBySuffix(suffix));
        fileDocument.setDocSuffix(suffix);
        fileDocument.setDocDescription(description);
        fileDocument.setDocContentTypeText(file.getContentType());

        // 上传文件到GridFs
        try {
            String gridFsId = uploadFileToGridFs(file.getInputStream(), file.getContentType());
            if (StrUtil.isEmpty(gridFsId)) {
                return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "上传异常，文件上传失败！");
            }
            fileDocument.setDocGridfsId(gridFsId);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        fileDocument.setDocUserid(userId);
        fileDocument.setDocTeamid(userInfo.getUserTeamId());
        fileDocument.setDocProperty(property);

        // 更新mysql数据库数据，开启事务
        int result = updateMysqlDataWhenUpload(userId, userInfo.getUserTeamId(), property, fileDocument);
        if (result == 0) {
            // 删除mongodb中的文件
            deleteFileByGridFsId(fileDocument.getDocGridfsId());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "mysql数据更新失败，文件上传失败！");
        }

        return R.success("上传成功！", fileDocument);
    }


    /**
     * @MethodName deleteFileByGridFsId
     * @Description 根据gridFsid删除mongodb中的文件
     * @Author travis-wei
     * @Data 2023/4/12
     * @param gridFsId
     * @Return void
     **/
    private void deleteFileByGridFsId(String gridFsId) {
        Query deleteQuery = new Query().addCriteria(Criteria.where(FILE_NAME).is(gridFsId));
        gridFsTemplate.delete(deleteQuery);
    }


    /**
     * @MethodName updateMysqlDataWhenUpload
     * @Description 更新mysql数据库中的数据信息
     * @Author travis-wei
     * @Data 2023/4/12
     * @param userId
     * @param teamId
     * @param property
     * @param fileDocument
     * @Return java.lang.Integer
     **/
    @GlobalTransactional
    public Integer updateMysqlDataWhenUpload(String userId, String teamId, String property,FileDocument fileDocument) {
        try {
            // 增加文件记录
            documentMapper.insert(fileDocument);
            // 增加个人文档数量
            dubboDocUpdateDataService.updateUserDocNumber(userId, property, "1");
            // 增加团队文档数量
            dubboDocUpdateDataService.updateTeamDocNumber(userId, property, "1");

        } catch (Exception e) {
            log.error(e.getMessage());
            return 0;
        }
        return 1;
    }


    /**
     * @MethodName uploadFileToGridFs
     * @Description 向mongodb中上传文件，返回gridFsId或者previewId
     * @Author travis-wei
     * @Data 2023/4/11
     * @param inputStream
     * @param contentType
     * @Return java.lang.String  上传失败返回null
     **/
    public String uploadFileToGridFs(InputStream inputStream, String contentType) {
        // 随机生成gridFsId
        String gridFsId = IdUtil.simpleUUID();

        // 向mongo中上传文件
        try {
            gridFsTemplate.store(inputStream, gridFsId, contentType);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return gridFsId;
    }


    /**
     * @MethodName searchFileByMd5
     * @Description 根据md5搜索团队文件中的文件信息，如果没有找到返回null
     * @Author travis-wei
     * @Data 2023/4/11
     * @param md5
     * @Return com.travis.filesbottle.document.entity.FileDocument
     **/
    @Override
    public FileDocument searchFileByMd5(String md5, String teamId) {
        QueryWrapper<FileDocument> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(FileDocument.DOC_MD5, md5).eq(FileDocument.DOC_TEAMID, teamId);
        return documentMapper.selectOne(queryWrapper);
    }


    /**
     * @MethodName getPreviewDocStream
     * @Description 通过sourceId预览文件，maybe 不支持在线预览 or pdf在线预览 or 源文件在线预览 or kkFileView的url在线预览
     * @Author travis-wei
     * @Data 2023/4/14
     * @param sourceId
     * @Return com.travis.filesbottle.common.utils.R<?>
     **/
    @Override
    public R<?> getPreviewDocStream(String sourceId) {

        // 一、首先查找该源文件信息是否存在，如果不存在直接返回文件不存在的error信息
        FileDocument fileDocument = getFileDocumentBySourceId(sourceId);
        if (fileDocument == null) {
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "无法找到该文件！");
        }
        // 二、【情况一：文件不支持在线预览】判断该源文件的类型是否支持在线预览，如果不支持在线预览，返回状态码 18905（document模块 + 不支持预览）
        Short typeCode = fileDocument.getDocFileTypeCode();
        if (typeCode == null || typeCode == 0 || typeCode == -1 || (typeCode >= 601 && typeCode <= 1000)) {
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.FILE_NOT_SUPPORT_PREVIEW);
        }
        // 三、分别处理支持预览的文件信息
        DownloadDocument downloadDocument = new DownloadDocument();

        if (typeCode >= 1 && typeCode <= 200) {
            // 【情况二：文件支持pdf预览文件预览】支持转为pdf文件进行在线预览

            // 3.1.1: 根据预览文件ID，获取预览文件流，如果获取预览文件流出现错误，返回错误信息
            R<byte[]> bytesByIdResult = getDocumentBytesById(fileDocument.getDocPreviewId());
            if (!BizCodeUtil.isCodeSuccess(bytesByIdResult.getCode())) {
                return bytesByIdResult;
            }
            // 3.1.2: 组装DownloadDocument（返回信息）
            downloadDocument.setDocGridfsId(fileDocument.getDocGridfsId());
            downloadDocument.setDocPreviewId(fileDocument.getDocPreviewId());
            downloadDocument.setDocName(fileDocument.getDocName());
            downloadDocument.setDocSize(fileDocument.getDocSize());
            downloadDocument.setDocContentTypeText(fileDocument.getDocContentTypeText());
            downloadDocument.setDocSuffix(fileDocument.getDocSuffix());
            downloadDocument.setDocFileTypeCode(fileDocument.getDocFileTypeCode());
            downloadDocument.setDocDescription(fileDocument.getDocDescription());
            // 获取到的预览文件字节流数据
            downloadDocument.setBytes(bytesByIdResult.getData());

        } else if (typeCode >= 201 && typeCode <= 400) {
            // 【情况三：文件支持源文件在线预览】支持返回源文件流，进行在线预览
            return getSourceDocStream(sourceId);

        } else if (typeCode >= 401 && typeCode <= 600) {
            // 【情况四：文件支持kkFileView在线预览】支持使用kkFileView进行在线预览

            // 组装DownloadDocument（返回信息）
            downloadDocument.setDocGridfsId(fileDocument.getDocGridfsId());
            downloadDocument.setDocPreviewId(fileDocument.getDocPreviewId());
            downloadDocument.setDocName(fileDocument.getDocName());
            downloadDocument.setDocSize(fileDocument.getDocSize());
            downloadDocument.setDocContentTypeText(fileDocument.getDocContentTypeText());
            downloadDocument.setDocSuffix(fileDocument.getDocSuffix());
            downloadDocument.setDocFileTypeCode(fileDocument.getDocFileTypeCode());
            downloadDocument.setDocDescription(fileDocument.getDocDescription());
            // TODO 判断这样做是否合理，如果一直请求该url就可能导致kkFileView服务宕机，考虑通过后端请求预览文件的URL，可以做限流
            // 获取kkFileView提供的预览文档的url
            downloadDocument.setPreviewUrl(fileDocument.getDocPreviewUrl());
        }
        return R.success(downloadDocument);
    }

    /**
     * @MethodName getSourceDocStream
     * @Description 通过源文件ID获取源文件字节流、为controller提供源文件下载
     * @Author travis-wei
     * @Data 2023/4/14
     * @param sourceId
     * @Return com.travis.filesbottle.common.utils.R<?>
     **/
    @Override
    public R<?> getSourceDocStream(String sourceId) {
        // 一、首先根据源文件ID，从mysql数据库中查询FileDocument信息；如果没有找到，返回error信息
        FileDocument fileDocument = getFileDocumentBySourceId(sourceId);
        if (fileDocument == null) {
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "无法找到该文件！");
        }
        // 二、获取源文件流，如果获取文件流出现错误，返回错误信息
        R<byte[]> bytesByIdResult = getDocumentBytesById(sourceId);
        if (!BizCodeUtil.isCodeSuccess(bytesByIdResult.getCode())) {
            return bytesByIdResult;
        }
        // 三、组装DownloadDocument信息（返回对象）
        DownloadDocument downloadDocument = new DownloadDocument();

        downloadDocument.setDocGridfsId(fileDocument.getDocGridfsId());
        downloadDocument.setDocPreviewId(fileDocument.getDocPreviewId());
        downloadDocument.setDocName(fileDocument.getDocName());
        downloadDocument.setDocSize(fileDocument.getDocSize());
        downloadDocument.setDocContentTypeText(fileDocument.getDocContentTypeText());
        downloadDocument.setDocSuffix(fileDocument.getDocSuffix());
        downloadDocument.setDocFileTypeCode(fileDocument.getDocFileTypeCode());
        downloadDocument.setDocDescription(fileDocument.getDocDescription());
        // 获取到的文件字节流数据
        downloadDocument.setBytes(bytesByIdResult.getData());

        return R.success(downloadDocument);
    }

    /**
     * @MethodName getDocumentBytesById
     * @Description 根据文件gridFsId获取文件流 (可以为源文件，也可以为预览文件)
     * @Author travis-wei
     * @Data 2023/4/18
     * @param gridFsId
     * @Return com.travis.filesbottle.common.utils.R<byte[]>
     **/
    private R<byte[]> getDocumentBytesById(String gridFsId) {
        Query query = new Query().addCriteria(Criteria.where(FILE_NAME).is(gridFsId));
        GridFSFile fsFile = gridFsTemplate.findOne(query);
        if (fsFile == null || fsFile.getObjectId() == null) {
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "文件未找到，请检查文件ID是否正确！");
        }

        // 存储文件字节流
        byte[] bytes = null;
        try {
            // 打开下载流对象，用于获取流对象
            GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(fsFile.getObjectId());
            if (downloadStream.getGridFSFile().getLength() > 0) {
                // 创建gridFsSource
                GridFsResource fsResource = new GridFsResource(fsFile, downloadStream);
                bytes = IoUtil.readBytes(fsResource.getInputStream());
            } else {
                return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.BAD_REQUEST, "文件下载流出现错误！");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return R.error(BizCodeEnum.MOUDLE_DOCUMENT, BizCodeEnum.UNKNOW, e.getMessage());
        }
        return R.success("文件字节流获取成功！", bytes);
    }


    /**
     * @MethodName getFileDocumentBySourceId
     * @Description 根据源文件ID从mysql数据库中获取FileDocument信息（源文件）
     * @Author travis-wei
     * @Data 2023/4/18
     * @param sourceId
     * @Return com.travis.filesbottle.document.entity.FileDocument
     **/
    private FileDocument getFileDocumentBySourceId(String sourceId) {
        QueryWrapper<FileDocument> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(FileDocument.DOC_GRIDFS_ID, sourceId);
        // 查找源文件ID对应的数据记录
        return documentMapper.selectOne(queryWrapper);
    }
}
