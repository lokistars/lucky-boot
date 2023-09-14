package com.lucky.config.demo.flow;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * 数据处理器 既表示生产者,又是订阅者一般作为数据的中转处理站,将数据处理后发给下一个订阅者
 *
 * @program: lucky
 * @description: 发布订阅处理器
 * @author: Loki
 * @data: 2023-09-14 23:39
 **/
public class IntProcessor extends SubmissionPublisher<Integer> implements Flow.Processor<Integer, Integer> {

    private Flow.Subscription subscription;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        System.out.println(Thread.currentThread().getName() + "| 处理器开始接受数据");
        this.subscription.request(1);
    }

    @Override
    public void onNext(Integer item) {
        // 接受到一个数据处理,筛选偶数 发送给 订阅者
        System.out.println("接受到数据: " + item);
        if (item % 2 == 0){
            this.submit(item);
        }
        // 处理完调用request再请求一个数据
        this.subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();

        // 告诉发布者, 后面不接受数据了
        this.subscription.cancel();
    }

    @Override
    public void onComplete() {
        System.out.println("处理完了!");
        this.close();
    }
}
