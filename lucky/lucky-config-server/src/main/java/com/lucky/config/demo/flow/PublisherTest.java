package com.lucky.config.demo.flow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SubmissionPublisher;

/**
 * @program: lucky
 * @description: 发布订阅测试
 * @author: Loki
 * @data: 2023-09-14 23:35
 **/
public class PublisherTest {


    public static void main(String[] args) throws Exception{
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // 发布者数据缓冲区,当缓冲区数据满了后submit进入阻塞方法
        SubmissionPublisher<Integer> publisher = new SubmissionPublisher<Integer>(executorService, 10);

        // 处理器 建立关系 发布者和处理器
        IntProcessor processor = new IntProcessor();
        publisher.subscribe(processor);

        // 最终订阅者
        IntSubscriber subscriber = new IntSubscriber();
        // 在处理器中添加订阅者,建立关系 处理器和订阅者
        processor.subscribe(subscriber);

        for (int i = 0; i < 10; i++) {
            publisher.submit(i);
        }
        publisher.close();
        executorService.shutdown();
        Thread.sleep(30000);
    }
}
