package com.travis.filesbottle.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.sql.Timestamp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author travis-wei
 * @since 2023-04-11
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dms_document")
@ApiModel(value = "Document对象", description = "")
public class FileDocument extends Model<FileDocument> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("文档自增ID")
    @TableId(value = "doc_zzid", type = IdType.AUTO)
    private Long docZzid;

    @ApiModelProperty("文档名称")
    @TableField("doc_name")
    private String docName;

    @ApiModelProperty("文档大小")
    @TableField("doc_size")
    private Double docSize;

    @ApiModelProperty("文档上传时间")
    @TableField("doc_upload_date")
    private Timestamp docUploadDate;

    @ApiModelProperty("文档md5(验证)")
    @TableField("doc_md5")
    private String docMd5;

    @ApiModelProperty("文档类型")
    @TableField("doc_content_type_text")
    private String docContentTypeText;

    @ApiModelProperty("文档类型码")
    @TableField("doc_file_type_code")
    private Short docFileTypeCode;

    @ApiModelProperty("文档后缀")
    @TableField("doc_suffix")
    private String docSuffix;

    @ApiModelProperty("文档描述")
    @TableField("doc_description")
    private String docDescription;

    @ApiModelProperty("文档mongo管理的gridfs ID")
    @TableField("doc_gridfs_id")
    private String docGridfsId;

    @ApiModelProperty("提供预览文档的ID")
    @TableField("doc_preview_id")
    private String docPreviewId;

    @ApiModelProperty("提供 kkFileView 提供预览的URL")
    @TableField("doc_preview_url")
    private String docPreviewUrl;

    @ApiModelProperty("文档上传状态(等待、正在、成功、失败)")
    @TableField("doc_state")
    private Byte docState;

    @ApiModelProperty("文档错误信息")
    @TableField("doc_error_message")
    private String docErrorMessage;

    @ApiModelProperty("文档审核状态(true、false)")
    @TableField("doc_reviewing")
    private Byte docReviewing;

    @ApiModelProperty("文档违禁词列表")
    @TableField("doc_prohibited_word")
    private String docProhibitedWord;

    @ApiModelProperty("文档所属用户ID")
    @TableField("doc_userid")
    private String docUserid;

    @ApiModelProperty("文档所属团队ID")
    @TableField("doc_teamid")
    private String docTeamid;

    @ApiModelProperty("文档开放性质")
    @TableField("doc_property")
    private String docProperty;

    @ApiModelProperty("视频文件加密密钥")
    @TableField("doc_enc_key")
    private String docEncKey;

    public static final String DOC_ZZID = "doc_zzid";

    public static final String DOC_NAME = "doc_name";

    public static final String DOC_SIZE = "doc_size";

    public static final String DOC_UPLOAD_DATE = "doc_upload_date";

    public static final String DOC_MD5 = "doc_md5";

    public static final String DOC_CONTENT_TYPE_TEXT = "doc_content_type_text";

    public static final String DOC_FILE_TYPE_CODE = "doc_file_type_code";

    public static final String DOC_SUFFIX = "doc_suffix";

    public static final String DOC_DESCRIPTION = "doc_description";

    public static final String DOC_GRIDFS_ID = "doc_gridfs_id";

    public static final String DOC_PREVIEW_ID = "doc_preview_id";

    public static final String DOC_PREVIEW_URL = "doc_preview_url";

    public static final String DOC_STATE = "doc_state";

    public static final String DOC_ERROR_MESSAGE = "doc_error_message";

    public static final String DOC_REVIEWING = "doc_reviewing";

    public static final String DOC_PROHIBITED_WORD = "doc_prohibited_word";

    public static final String DOC_USERID = "doc_userid";

    public static final String DOC_TEAMID = "doc_teamid";

    public static final String DOC_PROPERTY = "doc_property";

    public static final String DOC_ENC_KEY = "doc_enc_key";

    @Override
    public Serializable pkVal() {
        return this.docZzid;
    }
}
