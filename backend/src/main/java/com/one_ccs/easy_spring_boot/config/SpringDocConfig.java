package com.one_ccs.easy_spring_boot.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import com.one_ccs.easy_spring_boot.entity.vo.Result;
import com.one_ccs.easy_spring_boot.entity.vo.response.AuthorizeVO;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringDocConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("apiToken", new SecurityScheme()
                    .name(HttpHeaders.AUTHORIZATION)
                    .type(SecurityScheme.Type.HTTP)
                    .in(SecurityScheme.In.HEADER)
                    .scheme("Bearer")
                    .bearerFormat("JWT")
                )
            )
            .info(new Info()
                .title("Easy SpringBoot API")
                .description("基于 Java 17、SpringBoot 3.3.2 开发的后端框架，集成 MyBits-Plus、SpringDoc、SpringSecurity 等插件，旨在提供一个高效、易用的后端开发环境。该框架通过清晰的目录结构和模块化设计，帮助开发者快速构建和部署后端服务。")
                .version("v1.0.0")
                .license(new License()
                    .name("开源协议：MIT")
                    .url("https://www.github.com/one-ccs/easy_spring_boot/license")
                )
                .contact(new Contact()
                    .name("one-ccs@foxmail.com")
                    .email("one-ccs@foxmail.com")
                )
            )
            .externalDocs(new ExternalDocumentation()
                .description("博客：one-ccs")
                .url("https://blog.csdn.net/qq_43155814")
            )
            .security(List.of(new SecurityRequirement().addList("apiToken")));
    }

    /**
     * 自定义接口
     * @return
     */
    public OpenApiCustomizer sortTagsAlphabetically() {
        return openApi -> {
            this.authorizePathItems().forEach(openApi.getPaths()::addPathItem);
        };
    }

    @Bean
    GroupedOpenApi totalApi() {
        return GroupedOpenApi.builder()
            .group("0-总览")
            .pathsToMatch("/api/**")
            .addOpenApiCustomizer(sortTagsAlphabetically())
            .build();
    }

    @Bean
    GroupedOpenApi accountApi() {
        return GroupedOpenApi.builder()
            .group("1-用户")
            .pathsToMatch("/api/user/**")
            .build();
    }

    @Bean
    GroupedOpenApi RoleApi() {
        return GroupedOpenApi.builder()
            .group("2-角色")
            .pathsToMatch("/api/role/**")
            .build();
    }

    @Bean
    GroupedOpenApi fileApi() {
        return GroupedOpenApi.builder()
            .group("7-文件")
            .pathsToMatch("/api/file/**")
            .build();
    }

    /**
     * 手动添加登录登出接口
     * @return PathItems
     */
    private Map<String, PathItem> authorizePathItems(){
        Map<String, PathItem> map = new HashMap<>();

        map.put("/api/user/login", new PathItem()
            .post(new Operation()
                .tags(List.of("1-用户"))
                .summary("登录")
                .description("登录接口")
                .operationId("login")
                .addParametersItem(new QueryParameter()
                    .name("username")
                    .required(true)
                    .schema(new Schema<>()
                        .type("string")
                        .example("admin")
                    )
                )
                .security(new ArrayList<>())
                .addParametersItem(new QueryParameter()
                    .name("password")
                    .required(true)
                    .schema(new Schema<>()
                        .type("string")
                        .example("202cb962ac59075b964b07152d234b70")
                    )
                )
                .responses(new ApiResponses()
                    .addApiResponse("200", new ApiResponse()
                        .description("OK")
                        .content(new Content().addMediaType("*/*", new MediaType()
                            .example(Result.success("登录成功", new AuthorizeVO()))
                            .schema(new Schema<>()
                                .$ref("#/components/schemas/ResultObject")
                            )
                        ))
                    )
                )
            )
        );
        map.put("/api/user/logout", new PathItem()
            .post(new Operation()
                .tags(List.of("1-用户"))
                .summary("登出")
                .description("登出接口")
                .operationId("logout")
                .responses(new ApiResponses()
                    .addApiResponse("200", new ApiResponse()
                        .description("OK")
                        .content(new Content().addMediaType("*/*", new MediaType()
                            .example(Result.success("登出成功"))
                            .schema(new Schema<>()
                                .$ref("#/components/schemas/ResultObject")
                            )
                        ))
                    )
                )
            )

        );

        return map;
    }
}
