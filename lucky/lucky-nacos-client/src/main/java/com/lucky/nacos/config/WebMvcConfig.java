package com.lucky.nacos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebMvcConfig.class);

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        log.info("====================> 开始执行 视图拦截器");
        registry.addViewController("/").setViewName("/thymeleaf/index.html");
    }
}
