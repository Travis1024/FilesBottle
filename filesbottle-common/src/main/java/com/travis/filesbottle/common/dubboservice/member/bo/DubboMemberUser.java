package com.travis.filesbottle.common.dubboservice.member.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName DubboMemberUser
 * @Description Dubbo-用户基本信息BO
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/5
 */
@Data
@ApiModel(value = "DubboMemberUser", description = "dubbo-member User BO对象")
public class DubboMemberUser implements Serializable {

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("用户名字")
    private String userName;

    @ApiModelProperty("用户密码")
    private String userPassword;

}
