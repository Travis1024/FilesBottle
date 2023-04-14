package com.travis.filesbottle.document.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travis.filesbottle.common.dubboservice.document.DubboDocUpdateDataService;
import com.travis.filesbottle.common.dubboservice.document.DubboDocUserInfoService;
import com.travis.filesbottle.common.dubboservice.document.bo.DubboDocumentUser;
import com.travis.filesbottle.common.enums.BizCodeEnum;
import com.travis.filesbottle.common.utils.R;
import com.travis.filesbottle.document.entity.FileDocument;
import com.travis.filesbottle.document.enums.FileTypeEnum;
import com.travis.filesbottle.document.mapper.DocumentMapper;
import com.travis.filesbottle.document.service.DocumentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

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
        // 计算文件大小，单位为MB
        double docSize = file.getSize() / 1024.0 / 1024.0;
        fileDocument.setDocSize(docSize);
        fileDocument.setDocUploadDate(new Timestamp(new Date().getTime()));
        fileDocument.setDocMd5(fileMd5);
        fileDocument.setDocContentType(FileTypeEnum.getCodeByFileType(suffix));
        fileDocument.setDocSuffix(suffix);
        fileDocument.setDocDescription(description);

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
}
