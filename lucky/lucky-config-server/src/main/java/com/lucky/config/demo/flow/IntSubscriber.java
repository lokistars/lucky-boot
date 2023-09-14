package com.lucky.config.demo.flow;

import java.util.concurrent.Flow;

/**
 * @program: lucky
 * @description: 自定义一个订阅者
 * @author: Loki
 * @data: 2023-09-14 23:22
 **/
public class IntSubscriber implements Flow.Subscriber<Integer> {

    private Flow.Subscription subscription;

    /**
     * 在发布者接受订阅者的订阅动作之后，发布任何的订阅消息之前被调用,订阅令牌
     * @param subscription a new subscription
     */
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        // 第一次请求获取数据个数
        System.out.println(Thread.currentThread().getName()+"| 订阅,开始请求数据");
        this.subscription.request(1);
    }

    /**
     * 下一个待处理的数据项的处理函数
     * @param item the item 数据
     */
    @Override
    public void onNext(Integer item){
        try {
            // 消费者五秒消费一个
            Thread.sleep(2000);
        }catch (Exception e){}
        System.out.println(Thread.currentThread().getName()+ "| 订阅者接收数据:"+item);
        // 下一次请求获取数据个数, 如果不写将不在请求数据
        this.subscription.request(1);

    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("订阅者接收数据异常:"+throwable.getMessage());
        // 取消订阅,订阅者不在接受数据
        this.subscription.cancel();
    }

    /**
     * 完成事件
     */
    @Override
    public void onComplete() {
        System.out.println("订阅完成");
    }
}
