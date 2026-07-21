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
                        .description("二手手機販售與維修整合平台 REST API。會員密碼使用 BCrypt，受保護 API 使用 JWT Bearer 驗證。"));
    }
}
