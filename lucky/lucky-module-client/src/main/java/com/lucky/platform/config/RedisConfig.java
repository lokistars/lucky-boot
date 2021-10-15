package com.lucky.platform.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.UnknownHostException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Configuration
@RefreshScope
public class RedisConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class);
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.database}")
    private int database;
    @Value("${spring.redis.timeout}")
    private int timeout;


    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        /*Jackson2JsonRedisSerializer jackson = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson.setObjectMapper(om);
        template.setValueSerializer(jackson); //value 序列化采用jackson*/
        FastJsonRedisSerializer fastjson = new FastJsonRedisSerializer(Object.class);
        StringRedisSerializer redisSerializer = new StringRedisSerializer();
        template.setValueSerializer(redisSerializer);
        // 设置值（value）的序列化采用FastJsonRedisSerializer。
        template.setHashValueSerializer(redisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    private RedisClient getRedisClient() {

        RedisURI.Builder redisURI = RedisURI.builder();
        redisURI.withHost(host)
                .withPort(port)
                .withDatabase(database)
                .withTimeout(Duration.of(timeout, ChronoUnit.SECONDS));
        if (password != null) {
            redisURI.withPassword(password);
        }
        return RedisClient.create(redisURI.build());
    }

    @Bean
    public StatefulRedisConnection redisPole() {
        //  创建客户端
        RedisClient redisClient = getRedisClient();
        //超时时间 20秒
        redisClient.setDefaultTimeout(Duration.ofSeconds(20));
        //  创建线程安全的连接
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        // 创建同步命令
        //RedisCommands<String, String> redisCommands = connection.sync();
        return connection;
    }

    /*@Bean(name = "jedisPool")
    public JedisPool jedisPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        return new JedisPool(config,host,port,timeout,password);
    }
    @Bean
    @ConditionalOnMissingBean(RedisClient.class)
    public RedisClient redisClient(@Qualifier("jedisPool") JedisPool pool) {
        LOGGER.info("初始化……Redis Client==Host={},Port={}", host, port);
        RedisClient redisClient = new RedisClient();
        redisClient.setJedisPool(pool);
        return redisClient;
    }*/
}
