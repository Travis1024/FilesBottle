package com.travis.filesbottle.common.dubboservice.auth.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @ClassName DubboAuthUser
 * @Description Dubbo-用户权限BO定义
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/7
 */
@Data
@ApiModel(value = "DubboAuthUser", description = "dubbo-auth User BO对象")
public class DubboAuthUser implements Serializable {

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("用户名字")
    private String userName;

    @ApiModelProperty("用户新tokenMap")
    Map<String, Object> newTokenMap;
}
