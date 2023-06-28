package com.lucky.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    private CorsConfiguration buildConfig() {
        CorsConfiguration config = new CorsConfiguration();
        // SpringBoot升级到2.4之后版本需要使用
        config.addAllowedOriginPattern("*");
        config.addAllowedOrigin("*"); // 1允许任何域名使用
        config.addAllowedHeader("*"); // 2允许任何头
        config.addAllowedMethod("*"); // 3允许任何方法（post、get等）
        config.setAllowCredentials(true);
        return config;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig()); // 4  
        return new CorsFilter(source);
    }
}
