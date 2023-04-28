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
 * @since 2023-04-27
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("dms_folder")
@ApiModel(value = "Folder对象", description = "")
public class Folder extends Model<Folder> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("文件夹自增 Id")
    @TableId(value = "folder_zzid", type = IdType.ASSIGN_ID)
    private Long folderZzid;

    @ApiModelProperty("文件夹名称")
    @TableField("folder_name")
    private String folderName;

    @ApiModelProperty("文件夹编号")
    @TableField("folder_id")
    private String folderId;

    @ApiModelProperty("文件夹创建人 Id")
    @TableField("folder_creator")
    private String folderCreator;

    @ApiModelProperty("文件夹所属团队 Id")
    @TableField("folder_team")
    private String folderTeam;

    @ApiModelProperty("父文件夹 Id")
    @TableField("folder_parent_id")
    private String folderParentId;

    @ApiModelProperty("文件夹创建时间")
    @TableField("folder_create_time")
    private Timestamp folderCreateTime;

    @ApiModelProperty("文件夹层级")
    @TableField("folder_layer")
    private Integer folderLayer;

    @ApiModelProperty("文件夹路径")
    @TableField("folder_path")
    private String folderPath;

    @ApiModelProperty("文件夹密码")
    @TableField("folder_password")
    private String folderPassword;

    public static final String FOLDER_ZZID = "folder_zzid";

    public static final String FOLDER_NAME = "folder_name";

    public static final String FOLDER_ID = "folder_id";

    public static final String FOLDER_CREATOR = "folder_creator";

    public static final String FOLDER_TEAM = "folder_team";

    public static final String FOLDER_PARENT_ID = "folder_parent_id";

    public static final String FOLDER_CREATE_TIME = "folder_create_time";

    public static final String FOLDER_LAYER = "folder_layer";

    public static final String FOLDER_PATH = "folder_path";

    public static final String FOLDER_PASSWORD = "folder_password";

    @Override
    public Serializable pkVal() {
        return this.folderZzid;
    }
}
