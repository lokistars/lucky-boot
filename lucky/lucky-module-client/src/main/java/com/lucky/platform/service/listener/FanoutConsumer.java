package com.lucky.platform.service.listener;

import com.lucky.platform.config.RabbitMqConfig;
import com.lucky.platform.entity.User;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RabbitListener(queues = {RabbitMqConfig.FANOUT_QUEUE})
@Component
public class FanoutConsumer {

    @RabbitHandler
    public void fanoutMessage(User user){
        System.out.println("消费的消息是："+user);
    }
}
