package com.lucky.nacos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.spring.annotation.MapperScan;

import java.time.Duration;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.lucky.nacos.mapper")
public class NacosClientApplication {

    @Autowired
    private RestTemplateBuilder builder;
    @Bean
    public RestTemplate restTemplate(){
        //建立连接所用时间  15秒  服务器读取到可用资源所用的时间 15秒
        return builder.setConnectTimeout(Duration.ofSeconds(15)).setReadTimeout(Duration.ofSeconds(15)).build();
    }

    public static void main(String[] args) {
        SpringApplication.run(NacosClientApplication.class, args);
    }

}
