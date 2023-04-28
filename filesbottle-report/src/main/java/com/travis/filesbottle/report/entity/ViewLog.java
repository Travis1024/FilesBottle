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
@TableName("rms_view_log")
@ApiModel(value = "ViewLog对象", description = "")
public class ViewLog extends Model<ViewLog> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("浏览记录自增 Id")
    @TableId(value = "view_zzid", type = IdType.AUTO)
    private Long viewZzid;

    @ApiModelProperty("浏览的文件 Id")
    @TableField("view_document_id")
    private String viewDocumentId;

    @ApiModelProperty("浏览的文件名")
    @TableField("view_document_name")
    private String viewDocumentName;

    @ApiModelProperty("浏览的文件所属团队Id")
    @TableField("view_document_team")
    private String viewDocumentTeam;

    @ApiModelProperty("浏览人 Id")
    @TableField("view_person_id")
    private String viewPersonId;

    @ApiModelProperty("浏览人名字")
    @TableField("view_person_name")
    private String viewPersonName;

    @ApiModelProperty("文件浏览时间")
    @TableField("view_time")
    private Timestamp viewTime;

    public static final String VIEW_ZZID = "view_zzid";

    public static final String VIEW_DOCUMENT_ID = "view_document_id";

    public static final String VIEW_DOCUMENT_NAME = "view_document_name";

    public static final String VIEW_DOCUMENT_TEAM = "view_document_team";

    public static final String VIEW_PERSON_ID = "view_person_id";

    public static final String VIEW_PERSON_NAME = "view_person_name";

    public static final String VIEW_TIME = "view_time";

    @Override
    public Serializable pkVal() {
        return this.viewZzid;
    }
}
