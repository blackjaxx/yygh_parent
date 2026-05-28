package com.atguigu.yygh.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 文档（springdoc），替代原 springfox Swagger2。
 * 访问: /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi webApi() {
        return GroupedOpenApi.builder()
                .group("webApi")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("adminApi")
                .pathsToMatch("/admin/**")
                .build();
    }

    @Bean
    public OpenAPI yyghOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("尚医通 API")
                        .description("网站与后台管理微服务接口")
                        .version("1.0")
                        .contact(new Contact().name("atguigu").url("http://atguigu.com")));
    }
}
