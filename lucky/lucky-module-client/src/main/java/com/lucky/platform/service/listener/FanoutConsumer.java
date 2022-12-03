package com.lucky.platform.service.listener;

import com.lucky.platform.config.RabbitMqConfig;
import com.lucky.platform.entity.User;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class FanoutConsumer {

    /**
     * 注解形式处理
     * @param user
     * @param channel
     */
    @RabbitListener(queues = {RabbitMqConfig.FANOUT_QUEUE})
    @RabbitHandler
    public void fanoutMessage(User user, Channel channel){
        System.out.println("消费的消息是："+user);
    }

    /**
     * 通过监听器形式
     * @param connectionFactory
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer SimpleMessageContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(new Queue(""));    // 监听的队列
        container.setConcurrentConsumers(5);    // 当前的消费者数量
        container.setMaxConcurrentConsumers(8); // 最大的消费者数量
        container.setDefaultRequeueRejected(true); // 是否重回队列
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 签收模式 手动
        container.setExposeListenerChannel(true);
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {    // 消费端的标签策略
            @Override
            public String createConsumerTag(String queue) { //为每个动态的消费者定义一个tag
                return queue + "_" + UUID.randomUUID().toString();
            }
        });
        container.setMessageListener(new ChannelAwareMessageListener() {

            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                // 处理消息
            }
        }); //设置监听类
        container.start();
        return container;
    }

}
