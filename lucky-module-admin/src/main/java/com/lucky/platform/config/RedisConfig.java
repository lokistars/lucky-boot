package com.lucky.platform.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * @author Loki
 */
@Configuration
@ConditionalOnProperty(prefix="spring.profiles",name = "active",havingValue = "dev")
public class RedisConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class);

    @Autowired
    private RedisProperties redisProperties;

    /**
     * RedisConnectionFactory接口,实现子类：JedisConnectionFactory,LettuceConnectionConfiguration,RedissonConnectionFactory
     * 如果加载redisson-starter 会屏蔽Jedis跟Lettuce
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedissonConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(StringRedisSerializer.UTF_8);
        template.setValueSerializer(StringRedisSerializer.UTF_8);
        template.setHashKeySerializer(StringRedisSerializer.UTF_8);
        Jackson2JsonRedisSerializer<Object> jackson = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance ,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY);
        jackson.setObjectMapper(om);
        //new GenericJackson2JsonRedisSerializer()
        template.setHashValueSerializer(jackson);
        template.afterPropertiesSet();
        //设置redis支持数据库的事务
        template.setEnableTransactionSupport(false);
        //配置共享本地连接设置
        //factory.setShareNativeConnection(true);
        return template;
    }


    /**
     * RedissonAutoConfiguration 初始化 redisson redisson-spring-boot-starter
     * @return
     */
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://"+redisProperties.getHost()+":"+redisProperties.getPort())
                .setPassword(redisProperties.getPassword())
                .setDatabase(redisProperties.getDatabase())
                .setConnectionMinimumIdleSize(20);
        // 使用json序列化
        Codec codec = new JsonJacksonCodec();
        config.setCodec(codec);
        return Redisson.create(config);
    }
}
