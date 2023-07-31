package com.travis.filesbottle.minio.entity;

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
 * @since 2023-07-31
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("mms_minio")
@ApiModel(value = "Minio对象", description = "")
public class Minio extends Model<Minio> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("Minio 切片自增 ID")
    @TableId(value = "minio_zzid", type = IdType.ASSIGN_ID)
    private Long minioZzid;

    @ApiModelProperty("Minio 切片文件名称")
    @TableField("minio_file_name")
    private String minioFileName;

    @ApiModelProperty("Minio 切片文件 MD5")
    @TableField("minio_file_md5")
    private String minioFileMd5;

    @ApiModelProperty("Minio 切片文件上传状态")
    @TableField("minio_file_status")
    private Byte minioFileStatus;

    @ApiModelProperty("Minio 切片文件上传 ID (与minio-id对应)")
    @TableField("minio_upload_id")
    private String minioUploadId;

    @ApiModelProperty("Minio 总切片数量")
    @TableField("minio_total_chunk")
    private Integer minioTotalChunk;

    @ApiModelProperty("Minio 文件 url")
    @TableField("minio_file_url")
    private String minioFileUrl;

    public static final String MINIO_ZZID = "minio_zzid";

    public static final String MINIO_FILE_NAME = "minio_file_name";

    public static final String MINIO_FILE_MD5 = "minio_file_md5";

    public static final String MINIO_FILE_STATUS = "minio_file_status";

    public static final String MINIO_UPLOAD_ID = "minio_upload_id";

    public static final String MINIO_TOTAL_CHUNK = "minio_total_chunk";

    public static final String MINIO_FILE_URL = "minio_file_url";

    @Override
    public Serializable pkVal() {
        return this.minioZzid;
    }
}
