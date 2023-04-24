package com.travis.filesbottle.common.dubboservice.member.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName DubboDocumentUser
 * @Description Dubbo查询用户所属团队信息
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/11
 */
@Data
public class DubboDocumentUser implements Serializable {

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户所属团队ID")
    private String userTeamId;

    @ApiModelProperty(value = "用户封禁状态，0:正常，1:被封禁")
    private Byte userBanning;
}
