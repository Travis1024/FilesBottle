package com.travis.filesbottle.report.entity;

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
 * @since 2023-04-27
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("rms_download_log")
@ApiModel(value = "DownloadLog对象", description = "")
public class DownloadLog extends Model<DownloadLog> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("下载记录自增 Id")
    @TableId(value = "down_zzid", type = IdType.AUTO)
    private Long downZzid;

    @ApiModelProperty("下载文件 Id")
    @TableField("down_document_id")
    private String downDocumentId;

    @ApiModelProperty("下载文件名")
    @TableField("down_document_name")
    private String downDocumentName;

    @ApiModelProperty("下载文件所属团队")
    @TableField("down_document_team")
    private String downDocumentTeam;

    @ApiModelProperty("文件下载人Id")
    @TableField("down_person_id")
    private String downPersonId;

    @ApiModelProperty("文件下载人名字")
    @TableField("down_person_name")
    private String downPersonName;

    @ApiModelProperty("文件下载时间")
    @TableField("down_time")
    private Timestamp downTime;

    public static final String DOWN_ZZID = "down_zzid";

    public static final String DOWN_DOCUMENT_ID = "down_document_id";

    public static final String DOWN_DOCUMENT_NAME = "down_document_name";

    public static final String DOWN_DOCUMENT_TEAM = "down_document_team";

    public static final String DOWN_PERSON_ID = "down_person_id";

    public static final String DOWN_PERSON_NAME = "down_person_name";

    public static final String DOWN_TIME = "down_time";

    @Override
    public Serializable pkVal() {
        return this.downZzid;
    }
}
