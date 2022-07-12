package com.lucky.platform.rabbitMq;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;

/**
 * @author: Loki
 * @data: 2022-07-12 20:10
 */
public class HelloWorld {

    /**
     * hello world 使用的默认交换机,队列只能有一个消费者消费
     * @throws Exception
     */
    @Test
    public void helloPublish()throws Exception{
        // 获取连接
        final Connection connection = RabbitMqUtil.getConnection();
        // 构建Channel
        final Channel channel = connection.createChannel();
        // 构建队列
        channel.queueDeclare(RabbitMqUtil.HELLO_QUEUE,false,false,false,null);
        // 发布消息
        String message = "Hello World";
        channel.basicPublish("",RabbitMqUtil.HELLO_QUEUE,null,message.getBytes());
        System.out.println("消息发送成功");
        //System.in.read();
    }

    @Test
    public void helloConsumer() throws Exception{
        final Connection connection = RabbitMqUtil.getConnection();
        final Channel channel = connection.createChannel();
        channel.queueDeclare(RabbitMqUtil.HELLO_QUEUE,false,false,false,null);
        final DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者获取消息："+new String(body,"UTF-8"));
            }
        };
        channel.basicConsume(RabbitMqUtil.HELLO_QUEUE,true,consumer);
        System.out.println("开始监听队列");
        System.in.read();
    }

    /**
     *  使用work queue
     * @throws Exception
     */
    @Test
    public void workPublish()throws Exception{
        // 获取连接
        final Connection connection = RabbitMqUtil.getConnection();
        // 构建Channel
        final Channel channel = connection.createChannel();
        // 构建队列
        channel.queueDeclare(RabbitMqUtil.WORK_QUEUE,false,false,false,null);
        // 发布消息
        for (int i = 0; i < 10; i++) {
            String message = "Hello World"+i;
            channel.basicPublish("",RabbitMqUtil.WORK_QUEUE,null,message.getBytes());
        }
        System.out.println("消息发送成功");
        //System.in.read();
    }

    /**
     * 使用 work queue 一个队列可以有多个消费者
     * @throws Exception
     */
    @Test
    public void workConsumer1() throws Exception{
        final Connection connection = RabbitMqUtil.getConnection();
        final Channel channel = connection.createChannel(1);
        channel.queueDeclare(RabbitMqUtil.WORK_QUEUE,false,false,false,null);
        // 设置消息的流控,一次获取多少条消息
        channel.basicQos(1);
        final DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者一号获取消息："+new String(body,"UTF-8"));
                // 手动ack,告诉rabbitmq已经消费了
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };
        // false 关闭自动ack,默认情况下队列会以轮询的方式交给不同的消费者消费
        channel.basicConsume(RabbitMqUtil.WORK_QUEUE,false,consumer);
        System.out.println("开始监听队列");
        System.in.read();
    }

    @Test
    public void workConsumer2() throws Exception{
        final Connection connection = RabbitMqUtil.getConnection();
        final Channel channel = connection.createChannel();
        channel.queueDeclare(RabbitMqUtil.WORK_QUEUE,false,false,false,null);
        final DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者二号获取消息："+new String(body,"UTF-8"));
            }
        };
        channel.basicConsume(RabbitMqUtil.WORK_QUEUE,true,consumer);
        System.out.println("开始监听队列");
        System.in.read();
    }

    /**
     * publish/subscribe 发布订阅模式
     */
    @Test
    public void publish() throws Exception{
        // 1.获取连接
        final Connection connection = RabbitMqUtil.getConnection();
        // 2.构建Channel
        final Channel channel = connection.createChannel();
        // 3.构建交换机
        channel.exchangeDeclare(RabbitMqUtil.EXCHANGE_NAME,BuiltinExchangeType.FANOUT,false,false,false,null);
        // 4.构建队列
        channel.queueDeclare(RabbitMqUtil.PUBSUB_QUEUE,false,false,false,null);
        // 5.绑定交换机
        channel.queueBind(RabbitMqUtil.PUBSUB_QUEUE,RabbitMqUtil.EXCHANGE_NAME,"");
        // 6.发送消息到交换机

        channel.basicPublish(RabbitMqUtil.EXCHANGE_NAME,"",null,"publish/subscribe".getBytes());
        System.out.println("消息成功发送！！");
    }

    public void subscribe(){

    }
}
