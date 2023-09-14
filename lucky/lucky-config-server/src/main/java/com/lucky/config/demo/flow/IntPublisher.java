package com.lucky.config.demo.flow;

import java.util.concurrent.Flow;

/**
 * @program: lucky
 * @description: 自定义一个发布者
 * @author: Loki
 * @data: 2023-09-14 23:19
 **/
public class IntPublisher implements Flow.Publisher<Integer> {


    @Override
    public void subscribe(Flow.Subscriber<? super Integer> subscriber) {
        for (int i = 0; i < 5; i++) {
            // 将数据发给订阅者
            subscriber.onNext(i);
        }
        // 发送完成信号
        subscriber.onComplete();
    }
}
