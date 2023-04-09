package com.travis.filesbottle.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
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
@TableName("ums_teamlevel")
@ApiModel(value = "Teamlevel对象", description = "")
public class Teamlevel extends Model<Teamlevel> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("团队级别自增ID")
    @TableId(value = "teamlevel_zzid", type = IdType.AUTO)
    private Long teamlevelZzid;

    @ApiModelProperty("团队级别ID")
    @TableField("teamlevel_id")
    private Byte teamlevelId;

    @ApiModelProperty("团队级别名称")
    @TableField("teamlevel_name")
    private String teamlevelName;

    @ApiModelProperty("当前团队级别最大人数限制")
    @TableField("teamlevel_max_people_number")
    private Integer teamlevelMaxPeopleNumber;

    @ApiModelProperty("当前团队级别最大存储空间（MB）")
    @TableField("teamlevel_max_storage_space")
    private Long teamlevelMaxStorageSpace;

    public static final String TEAMLEVEL_ZZID = "teamlevel_zzid";

    public static final String TEAMLEVEL_ID = "teamlevel_id";

    public static final String TEAMLEVEL_NAME = "teamlevel_name";

    public static final String TEAMLEVEL_MAX_PEOPLE_NUMBER = "teamlevel_max_people_number";

    public static final String TEAMLEVEL_MAX_STORAGE_SPACE = "teamlevel_max_storage_space";

    @Override
    public Serializable pkVal() {
        return this.teamlevelZzid;
    }
}
