package com.lucky.config.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @program: lucky
 * @description: 基于1.8源码分析, 一般作用于流控制, 每当有一个线程获取资源时对信号量(state)-1,当信号量为0时候进行阻塞
 * @author: Loki
 * @data: 2023-06-15 22:10
 **/
public class MySemaphore implements Serializable {

    private static final long serialVersionUID = -3222578661600680210L;

    private final MySemaphore.Sync sync;

    /**
     * @param permits 声明型号量,默认不公平同步
     */
    public MySemaphore(int permits) {
        sync = new MySemaphore.NonfairSync(permits);
    }

    /**
     * 公平: 有排队就进行排队,没有直接进行抢占资源
     * 非公平: 不管有没有排队直接抢占,没有抢到在进行排队
     *
     * @param permits 声明型号量
     * @param fair    true 公平同步的, false 非公平同步
     */
    public MySemaphore(int permits, boolean fair) {
        sync = fair ? new MySemaphore.FairSync(permits) : new MySemaphore.NonfairSync(permits);
    }

    /**
     * 获取共享锁并允许中断
     *
     * @throws InterruptedException
     */
    public void acquire() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    public void acquireUninterruptibly() {
        sync.acquireShared(1);
    }

    public boolean tryAcquire() {
        return sync.nonfairTryAcquireShared(1) >= 0;
    }

    public boolean tryAcquire(long timeout, TimeUnit unit)
            throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    /**
     * 归还信号量,对state+1
     */
    public void release() {
        sync.releaseShared(1);
    }

    /**
     * 获取资源,对信号量state-1
     */
    public void acquire(int permits) throws InterruptedException {
        if (permits < 0) {
            throw new IllegalArgumentException();
        }
        sync.acquireSharedInterruptibly(permits);
    }

    public void acquireUninterruptibly(int permits) {
        if (permits < 0) {
            throw new IllegalArgumentException();
        }
        sync.acquireShared(permits);
    }

    public boolean tryAcquire(int permits) {
        if (permits < 0){
            throw new IllegalArgumentException();
        }
        return sync.nonfairTryAcquireShared(permits) >= 0;
    }

    public boolean tryAcquire(int permits, long timeout, TimeUnit unit)
            throws InterruptedException {
        if (permits < 0) {
            throw new IllegalArgumentException();
        }
        return sync.tryAcquireSharedNanos(permits, unit.toNanos(timeout));
    }

    public void release(int permits) {
        if (permits < 0) {
            throw new IllegalArgumentException();
        }
        sync.releaseShared(permits);
    }

    public int availablePermits() {
        return sync.getPermits();
    }

    public int drainPermits() {
        return sync.drainPermits();
    }

    protected void reducePermits(int reduction) {
        if (reduction < 0){
            throw new IllegalArgumentException();
        }
        sync.reducePermits(reduction);
    }

    public boolean isFair() {
        return sync instanceof MySemaphore.FairSync;
    }

    public final boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    public final int getQueueLength() {
        return sync.getQueueLength();
    }

    protected Collection<Thread> getQueuedThreads() {
        return sync.getQueuedThreads();
    }

    public String toString() {
        return super.toString() + "[Permits = " + sync.getPermits() + "]";
    }

    abstract static class Sync extends MyAbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1192457210091910933L;

        Sync(int permits) {
            setState(permits);
        }

        final int getPermits() {
            return getState();
        }

        /**
         * 非公平方式,直接对资源进行 -1
         */
        final int nonfairTryAcquireShared(int acquires) {
            for (; ; ) {
                int available = getState();
                int remaining = available - acquires;
                // 如果资源不够直接返回, 资源够修改state
                if (remaining < 0 || compareAndSetState(available, remaining)) {
                    return remaining;
                }
            }
        }

        /**
         * 对资源进行归还,信号量(state)+1
         */
        protected final boolean tryReleaseShared(int releases) {
            for (; ; ) {
                int current = getState();
                int next = current + releases;
                if (next < current) {
                    throw new Error("Maximum permit count exceeded");
                }
                if (compareAndSetState(current, next)) {
                    return true;
                }
            }
        }

        final void reducePermits(int reductions) {
            for (; ; ) {
                int current = getState();
                int next = current - reductions;
                if (next > current) {
                    throw new Error("Permit count underflow");
                }
                if (compareAndSetState(current, next)) {
                    return;
                }
            }
        }

        final int drainPermits() {
            for (; ; ) {
                int current = getState();
                if (current == 0 || compareAndSetState(current, 0)) {
                    return current;
                }
            }
        }

    }

    static final class NonfairSync extends MySemaphore.Sync {
        private static final long serialVersionUID = -2694183684443567898L;

        NonfairSync(int permits) {
            super(permits);
        }

        /**
         * 非公平
         */
        @Override
        protected int tryAcquireShared(int acquires) {
            return nonfairTryAcquireShared(acquires);
        }
    }

    static final class FairSync extends MySemaphore.Sync {
        private static final long serialVersionUID = 2014338818796000944L;

        FairSync(int permits) {
            super(permits);
        }

        @Override
        protected int tryAcquireShared(int acquires) {
            for (; ; ) {
                // 公平方式,先看队列中是否存在排队的,有排队直接返回,没有排队对资源数进行 -1
                if (hasQueuedPredecessors())
                    return -1;
                int available = getState();
                int remaining = available - acquires;
                // 如果资源不够直接返回, 资源够修改state
                if (remaining < 0 || compareAndSetState(available, remaining)) {
                    return remaining;
                }

            }
        }
    }


}
