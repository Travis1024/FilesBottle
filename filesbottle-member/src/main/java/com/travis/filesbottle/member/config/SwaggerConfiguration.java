package com.travis.filesbottle.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @ClassName SwaggerConfiguration
 * @Description Swagger配置类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/5
 */
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfiguration {
    @Bean(value = "userApi")
    @Order(value = 1)
    public Docket groupRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(groupApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.travis.filesbottle.member.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public ApiInfo groupApiInfo(){
        return new ApiInfoBuilder()
                .title("FilesBottle --> 人员及团队管理模块接口文档")
                .description("<div style='font-size:14px;color:red;'>FilesBottle --> create by travis-wei</div>")
                .contact(new Contact("travis", "https://travis1024.github.io/", "travis_x@163.com"))
                .version("1.0")
                .build();
    }

}
