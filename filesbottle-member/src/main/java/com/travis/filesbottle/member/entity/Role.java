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
@TableName("ums_role")
@ApiModel(value = "Role对象", description = "")
public class Role extends Model<Role> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("角色自增ID")
    @TableId(value = "role_zzid", type = IdType.AUTO)
    private Long roleZzid;

    @ApiModelProperty("角色ID")
    @TableId(value = "role_id", type = IdType.ASSIGN_ID)
    private Byte roleId;

    @ApiModelProperty("角色名称")
    @TableField("role_name")
    private String roleName;

    public static final String ROLE_ZZID = "role_zzid";

    public static final String ROLE_ID = "role_id";

    public static final String ROLE_NAME = "role_name";

    @Override
    public Serializable pkVal() {
        return this.roleId;
    }
}
