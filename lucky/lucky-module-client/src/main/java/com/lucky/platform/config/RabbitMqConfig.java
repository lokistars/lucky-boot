package com.lucky.platform.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author loki
 * @data: 2022-08-09 21:14
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 队列名字
     */
    public static final String HELLO_QUEUE = "hello";
    public static final String WORK_QUEUE = "work";
    public static final String FANOUT_QUEUE = "fanout";
    public static final String FANOUT_QUEUE1 = "fanout1";
    public static final String ROUTING_QUEUE = "routing";
    public static final String TOPIC_QUEUE = "topic";
    public static final String HEADERS_QUEUE = "headers";

    /**
     * 交换机名字
     */
    public static final String DIRECT_NAME = "direct";
    public static final String FANOUT_NAME = "fanout";
    public static final String TOPIC_NAME = "topic";
    public static final String HEADERS_NAME = "headers";

    /**
     * 声明fanout交换机
     * @return
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_NAME,false,false,null);
    }

    /**
     * 声明队列
     * @return
     */
    @Bean
    public Queue fanoutQueue(){
        return new Queue(FANOUT_QUEUE,false,false,false,null);
    }

    /**
     * 交换机和队列进行绑定
     * @return
     */
    @Bean
    public Binding fanoutBinding(){
        return BindingBuilder.bind(fanoutQueue()).to(fanoutExchange());
    }
}
