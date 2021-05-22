package com.lucky.juc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

/**
 * ReentrantLock 实现线程切换, 按顺序打印ABC
 * @author: Loki
 * @data: 2021-05-22 10:57
 **/
public class AwaitSignalTest {

    public static void main(String[] args) throws InterruptedException {
        AtomicReference<String> atr = new AtomicReference<>("a");

        AwaitSignal awaitSignal = new AwaitSignal(3);
        Condition a = awaitSignal.newCondition();
        Condition b = awaitSignal.newCondition();
        Condition c = awaitSignal.newCondition();
        new Thread(()->{
            awaitSignal.print("a",a,b);
        },"a").start();
        new Thread(()->{
            awaitSignal.print("b",b,c);
        },"a").start();
        new Thread(()->{
            awaitSignal.print("c",c,a);
        },"a").start();
        TimeUnit.SECONDS.sleep(1);
        awaitSignal.lock();
        try {
            System.out.println("开始了。。");
            a.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            awaitSignal.unlock();
        }
        demo(   ()-> new AtomicLong(0),
                (adder)->adder.getAndDecrement()
        );
    }

    private static <T> void demo(Supplier<T> supplier, Consumer<T> action){
        T adder = supplier.get();
        action.accept(adder);
    }
}

class AwaitSignal extends ReentrantLock{
    // 打印次数
    private int loopNumber;

    public AwaitSignal(int loopNumber) {
        this.loopNumber = loopNumber;
    }
    // 需要打印的内容, 需要等待的线程,下一个需求执行的线程
    public void print(String str, Condition current,Condition next){
        for (int i = 0; i < this.loopNumber; i++) {
            try {
                lock();
                current.await();//当前线程阻塞
                System.out.print(str);
                next.signal(); // 下一个线程唤醒
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                unlock();
            }
        }
    }
}