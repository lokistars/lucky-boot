package com.lucky.config.util;

import java.util.concurrent.TimeUnit;

/**
 * @program: lucky
 * @description: 基于1.8源码分析
 * @author: Loki
 * @data: 2023-06-15 22:00
 **/
public class MyCountDownLatch {

    private final MyCountDownLatch.Sync sync;

    /**
     * 构造一个用给定计数初始化的
     *
     * @param count
     */
    public MyCountDownLatch(int count) {
        if (count < 0) throw new IllegalArgumentException("count < 0");
        // 给AQS的state赋值
        this.sync = new MyCountDownLatch.Sync(count);
    }

    public void await() throws InterruptedException {
        // 调用了AQS提供的获取共享锁并且允许中断方法
        sync.acquireSharedInterruptibly(1);
    }

    /**
     * 当前线程等待，直到锁存器倒计时为零
     *
     * @param timeout 等待的最长时间
     * @param unit    时间单位
     * @return
     * @throws InterruptedException
     */
    public boolean await(long timeout, TimeUnit unit)
            throws InterruptedException {
        // 尝试获取共享锁
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    /**
     * 减少锁存器的计数，如果计数达到零，则释放所有等待线程
     */
    public void countDown() {
        // 本质就是调用了释放共享锁操作
        sync.releaseShared(1);
    }

    /**
     * 返回当前计数。
     *
     * @return
     */
    public long getCount() {
        return sync.getCount();
    }

    public String toString() {
        return super.toString() + "[Count = " + sync.getCount() + "]";
    }

    /**
     * CountDownLatch的同步控制。使用AQS state表示计数
     */
    private static final class Sync extends MyAbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        Sync(int count) {
            setState(count);
        }

        int getCount() {
            return getState();
        }

        protected int tryAcquireShared(int acquires) {
            // state 为0 返回1 ,否则返回-1
            return (getState() == 0) ? 1 : -1;
        }

        protected boolean tryReleaseShared(int releases) {
            // 递减计数; 转换到零时的信号
            for (; ; ) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c - 1;
                // 通过aqs对state - 1
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }

}
