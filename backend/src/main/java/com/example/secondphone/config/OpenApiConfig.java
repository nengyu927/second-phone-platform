package com.example.secondphone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI secondPhonePlatformOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Second Phone Platform API")
                        .version("1.0.0")
                        .description("二手手機販售與維修整合平台 REST API。"
                                + "目前為課堂展示版本，密碼欄位尚未加密且可能出現在 API 回應；"
                                + "正式版本應使用 BCrypt 並透過 DTO 隱藏 password。"));
    }
}
