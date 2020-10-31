package com.lucky.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * @author 53276
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@MapperScan("com.lucky.platform.mapper")
public class ClientApplication {
    private static final Logger log = LoggerFactory.getLogger(ClientApplication.class);
    /*@Autowired
    private RestTemplateBuilder builder;
    @Bean
    public RestTemplate restTemplate(){
        //建立连接所用时间  15秒  服务器读取到可用资源所用的时间 15秒
        return builder.setConnectTimeout(Duration.ofSeconds(15)).setReadTimeout(Duration.ofSeconds(15)).build();
    }*/

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
        log.info("ClientApplication -- start");
    }

}
