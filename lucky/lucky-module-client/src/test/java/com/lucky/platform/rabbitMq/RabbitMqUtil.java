package com.lucky.platform.rabbitMq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author: Loki
 * @data: 2022-07-12 20:31
 */
public class RabbitMqUtil {
    private static final String RABBITMQ_HOST = "119.91.148.176";
    private static final Integer RABBITMQ_PORT = 5672;
    private static final String RABBITMQ_USERNAME = "lucky";
    private static final String RABBITMQ_PASSWORD = "lucky";

    public static final String HELLO_QUEUE = "hello";
    public static final String WORK_QUEUE = "work";
    public static final String PUBSUB_QUEUE = "pubsub";
    public static final String ROUTING_QUEUE = "routing";
    public static final String TOPIC_QUEUE = "topic";
    public static final String RPC_QUEUE = "rpc";


    public static final String EXCHANGE_NAME = "pubsub";
    public static Connection getConnection() throws Exception{
        // 创建connection工厂
        final ConnectionFactory factory = new ConnectionFactory();
        // 设置RabbitMq连接信息
        factory.setHost(RABBITMQ_HOST);
        factory.setPort(RABBITMQ_PORT);
        factory.setUsername(RABBITMQ_USERNAME);
        factory.setPassword(RABBITMQ_PASSWORD);
        factory.setVirtualHost("/");
        // 返回连接对象
        return factory.newConnection();
    }

}
