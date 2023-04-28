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
@TableName("rms_login_log")
@ApiModel(value = "LoginLog对象", description = "")
public class LoginLog extends Model<LoginLog> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("登录记录 Id")
    @TableId(value = "login_zzid", type = IdType.AUTO)
    private Long loginZzid;

    @ApiModelProperty("登录人 Id")
    @TableField("login_person_id")
    private String loginPersonId;

    @ApiModelProperty("登录人名字")
    @TableField("login_person_name")
    private String loginPersonName;

    @ApiModelProperty("登录时间")
    @TableField("login_time")
    private Timestamp loginTime;

    @ApiModelProperty("登录 ip")
    @TableField("login_ip")
    private String loginIp;

    @ApiModelProperty("登录途径(app、web、小程序等)")
    @TableField("login_way")
    private String loginWay;

    public static final String LOGIN_ZZID = "login_zzid";

    public static final String LOGIN_PERSON_ID = "login_person_id";

    public static final String LOGIN_PERSON_NAME = "login_person_name";

    public static final String LOGIN_TIME = "login_time";

    public static final String LOGIN_IP = "login_ip";

    public static final String LOGIN_WAY = "login_way";

    @Override
    public Serializable pkVal() {
        return this.loginZzid;
    }
}
