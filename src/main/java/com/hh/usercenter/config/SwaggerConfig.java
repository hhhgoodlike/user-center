package com.hh.usercenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 自定义swagger接口文档的配置
 *
 * @author hh
 */

@Configuration
// 指定扫描的 api 包路径
@ComponentScan()
//注解开启 swagger2 功能
@EnableSwagger2
public class SwaggerConfig {


//    @Value("${swagger2.enable}")
//    boolean enable;
    // 配置文件中通过值注入控制生产环境与开发环境下的启用状态


    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.hh.usercenter.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * api信息
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("用户管理中心")//标题
                .description("用户管理中心接口文档")//描述
                .termsOfServiceUrl("https://github.com/hhhgoodlike/user-center")
                .contact(new Contact("hh","https://github.com/hhhgoodlike/user-center","1870963557@qq.com"))//作者信息
                .version("1.0.0")//版本号
                .build();
    }


}