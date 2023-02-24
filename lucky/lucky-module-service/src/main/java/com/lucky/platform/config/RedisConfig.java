package com.lucky.platform.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author Loki
 */
@Configuration
@ConditionalOnProperty(prefix="spring.profiles",name = "active",havingValue = "dev")
public class RedisConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class);


    /**
     * RedissonAutoConfiguration 初始化 redisson redisson-spring-boot-starter
     * @return
     */
    @Bean
    public RedissonClient redissonClient(RedisProperties properties){
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://"+properties.getHost()+":"+properties.getPort())
                .setPassword(properties.getPassword())
                .setDatabase(properties.getDatabase())
                .setConnectionMinimumIdleSize(20);

        Codec codec = new JsonJacksonCodec();
        config.setCodec(codec);
        return Redisson.create(config);
    }

    public static RedissonClient getInstance(){
        RedisConfig config = new RedisConfig();
        RedisProperties properties = new RedisProperties();
        properties.setHost("192.168.10.8");
        properties.setPort(6379);
        properties.setPassword("lucky5200");
        properties.setDatabase(0);
        return  config.redissonClient(properties);
    }

}
