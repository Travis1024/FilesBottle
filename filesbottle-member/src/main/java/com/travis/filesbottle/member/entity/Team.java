package com.travis.filesbottle.member.entity;

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
 * @since 2023-04-05
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ums_team")
@ApiModel(value = "Team对象", description = "")
public class Team extends Model<Team> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("团队自增ID")
    @TableId(value = "team_zzid", type = IdType.AUTO)
    private Long teamZzid;

    @ApiModelProperty("团队ID")
    @TableField(value = "team_id")
    private String teamId;

    @ApiModelProperty("团队名称")
    @TableField("team_name")
    private String teamName;

    @ApiModelProperty("团队创建者")
    @TableField("team_creator")
    private String teamCreator;

    @ApiModelProperty("团队创建时间")
    @TableField("team_create_time")
    private Timestamp teamCreateTime;

    @ApiModelProperty("团队级别")
    @TableField("team_level")
    private Byte teamLevel;

    @ApiModelProperty("团队人数")
    @TableField("team_people_number")
    private Integer teamPeopleNumber;

    @ApiModelProperty("团队描述信息")
    @TableField("team_description")
    private String teamDescription;

    @ApiModelProperty("团队是否启用")
    @TableField("team_enable")
    private Byte teamEnable;

    @ApiModelProperty("团队开放文档数量")
    @TableField("team_doc_public_number")
    private Integer teamDocPublicNumber;

    @ApiModelProperty("团队私有文档数量")
    @TableField("team_doc_private_number")
    private Integer teamDocPrivateNumber;

    public static final String TEAM_ZZID = "team_zzid";

    public static final String TEAM_ID = "team_id";

    public static final String TEAM_NAME = "team_name";

    public static final String TEAM_CREATOR = "team_creator";

    public static final String TEAM_CREATE_TIME = "team_create_time";

    public static final String TEAM_LEVEL = "team_level";

    public static final String TEAM_PEOPLE_NUMBER = "team_people_number";

    public static final String TEAM_DESCRIPTION = "team_description";

    public static final String TEAM_ENABLE = "team_enable";

    public static final String TEAM_DOC_PUBLIC_NUMBER = "team_doc_public_number";

    public static final String TEAM_DOC_PRIVATE_NUMBER = "team_doc_private_number";

    @Override
    public Serializable pkVal() {
        return this.teamZzid;
    }
}
