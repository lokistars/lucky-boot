package com.lucky.platform.config;


import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableKnife4j
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI createRestApi() {
        return new OpenAPI()
                .info(new Info().title("SpringBoot API 管理")
                        .contact(new Contact().name("Loki").email("xxxx@163.com").url("https://blog.csdn.net/N_007"))
                        .version("1.0")
                        .termsOfService("http://doc.xiaominfo.com")
                        .description("SpringBoot 集成 Knife4j 示例")
                        .license(new License().name("Apache 2.0")));
    }
}
