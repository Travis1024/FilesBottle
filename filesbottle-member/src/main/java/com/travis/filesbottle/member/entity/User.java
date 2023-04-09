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
@TableName("ums_user")
@ApiModel(value = "User对象", description = "")
public class User extends Model<User> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户自增ID")
    @TableId(value = "user_zzid", type = IdType.AUTO)
    private Long userZzid;

    @ApiModelProperty("用户ID")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("用户名字")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("用户密码")
    @TableField("user_password")
    private String userPassword;

    @ApiModelProperty("用户角色")
    @TableField("user_role")
    private Byte userRole;

    @ApiModelProperty("用户所属团队")
    @TableField("user_team")
    private String userTeam;

    @ApiModelProperty("用户团队角色")
    @TableField("user_team_role")
    private Byte userTeamRole;

    @ApiModelProperty("用户是否启用")
    @TableField("user_enable")
    private Byte userEnable;

    @ApiModelProperty("用户封禁状态")
    @TableField("user_banning")
    private Byte userBanning;

    @ApiModelProperty("用户性别")
    @TableField("user_gender")
    private Byte userGender;

    @ApiModelProperty("用户创建时间")
    @TableField("user_create_time")
    private Timestamp userCreateTime;

    @ApiModelProperty("用户上次登录时间")
    @TableField("user_login_time")
    private Timestamp userLoginTime;

    @ApiModelProperty("用户头像图片地址")
    @TableField("user_pic_url")
    private String userPicUrl;

    @ApiModelProperty("用户手机号码")
    @TableField("user_phone")
    private String userPhone;

    @ApiModelProperty("用户邮箱地址")
    @TableField("user_email")
    private String userEmail;

    @ApiModelProperty("用户是否隐藏手机号")
    @TableField("user_phone_hide")
    private Byte userPhoneHide;

    @ApiModelProperty("用户是否隐藏邮箱")
    @TableField("user_email_hide")
    private Byte userEmailHide;

    @ApiModelProperty("用户发布的公共文件数量")
    @TableField("user_doc_public_number")
    private Integer userDocPublicNumber;

    @ApiModelProperty("用户发布的团队文件数量")
    @TableField("user_doc_private_number")
    private Integer userDocPrivateNumber;

    public static final String USER_ZZID = "user_zzid";

    public static final String USER_ID = "user_id";

    public static final String USER_NAME = "user_name";

    public static final String USER_PASSWORD = "user_password";

    public static final String USER_ROLE = "user_role";

    public static final String USER_TEAM = "user_team";

    public static final String USER_TEAM_ROLE = "user_team_role";

    public static final String USER_ENABLE = "user_enable";

    public static final String USER_BANNING = "user_banning";

    public static final String USER_GENDER = "user_gender";

    public static final String USER_CREATE_TIME = "user_create_time";

    public static final String USER_LOGIN_TIME = "user_login_time";

    public static final String USER_PIC_URL = "user_pic_url";

    public static final String USER_PHONE = "user_phone";

    public static final String USER_EMAIL = "user_email";

    public static final String USER_PHONE_HIDE = "user_phone_hide";

    public static final String USER_EMAIL_HIDE = "user_email_hide";

    public static final String USER_DOC_PUBLIC_NUMBER = "user_doc_public_number";

    public static final String USER_DOC_PRIVATE_NUMBER = "user_doc_private_number";

    @Override
    public Serializable pkVal() {
        return this.userZzid;
    }
}
