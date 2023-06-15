package com.lucky.config.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 基于1.8源码
 * @author: Loki
 * @data: 2022-05-30 11:01
 */
public class MyReentrantLock implements Lock, Serializable {

    private final Sync sync;

    abstract static class Sync extends MyAbstractQueuedSynchronizer {
        private static final long serialVersionUID = -5179523762034025860L;

        /**
         * Performs {@link Lock#lock}. The main reason for subclassing
         * is to allow fast path for nonfair version.
         * 获取锁的方法
         */
        abstract void lock();

        /**
         * 执行不公平的 tryLock。 tryAcquire 在子类中实现，
         * 但两者都需要对 trylock 方法进行非公平尝试。
         */
        final boolean nonfairTryAcquire(int acquires) {
            // 当前线程
            final Thread current = Thread.currentThread();
            // 获取锁的状态
            int c = getState();
            if (c == 0) {
                // 没有线程正在持有锁,比较并且设置锁的状态,状态0表示没有被占有,不管有没有线程在排队,先抢一波
                if (compareAndSetState(0, acquires)) {
                    // 设置当前线程独占锁,成功返回
                    setExclusiveOwnerThread(current);
                    return true;
                }
                // 当前线程已经获取到锁,直接返回,增加重入次数
            } else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                // 判断重入锁是否超过了最大限制
                if (nextc < 0) {
                    throw new Error("Maximum lock count exceeded");
                }
                // 设置锁的状态
                setState(nextc);
                return true;
            }
            return false;
        }

        /**
         * 试图在共享模式下获取对象状态，此方法应该查询是否允许它在共享模式下获取对象状态，如果允许，则获取它
         * @param releases
         * @return 锁资源释放干净返回 true
         */
        @Override
        protected final boolean tryRelease(int releases) {
            // 获取state - 1
            int c = getState() - releases;
            // 如果释放的线程不是当前线程，则抛出异常
            if (Thread.currentThread() != getExclusiveOwnerThread()) {
                throw new IllegalMonitorStateException();
            }
            // 设置释放标识
            boolean free = false;
            // 锁资源释放干净了。
            if (c == 0) {
                free = true;
                // 已经释放了所有的锁，清除线程标识
                setExclusiveOwnerThread(null);
            }
            // 设置锁的状态
            setState(c);
            return free;
        }

        /**
         * 该线程是否正在独占资源。只有用到Condition才需要去实现它
         * 校验资源是否当前线程被占有
         * @return
         */
        @Override
        protected final boolean isHeldExclusively() {
            // While we must in general read state before owner,
            // we don't need to do so to check if current thread is owner
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        /**
         * 新生一个条件
         * @return
         */
        final ConditionObject newCondition() {
            return new ConditionObject();
        }

        /**
         * Methods relayed from outer class
         * 返回被占有的线程
         * @return
         */
        final Thread getOwner() {
            return getState() == 0 ? null : getExclusiveOwnerThread();
        }

        /**
         * 返回状态
         * @return
         */
        final int getHoldCount() {
            return isHeldExclusively() ? getState() : 0;
        }

        /**
         * 校验是否被占有
         * @return
         */
        final boolean isLocked() {
            return getState() != 0;
        }

        /**
         * Reconstitutes the instance from a stream (that is, deserializes it).
         * 自定义反序列化逻辑
         */
        private void readObject(java.io.ObjectInputStream s)
                throws java.io.IOException, ClassNotFoundException {
            s.defaultReadObject();
            setState(0); // reset to unlocked state
        }
    }

    /**
     * 采用非公平策略获取锁
     */
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        /**
         * Performs lock.  Try immediate barge, backing up to normal
         * acquire on failure.
         * 获取锁
         */
        @Override
        final void lock() {
            // 通过CAS设置变量State（同步状态）成功，也就是获取锁成功，则将当前线程设置为独占线程。
            // 通过CAS设置变量State（同步状态）失败，也就是获取锁失败，则进入Acquire方法进行后续处理。
            if (compareAndSetState(0, 1)) {
                // 设置当前线程为独占线程
                setExclusiveOwnerThread(Thread.currentThread());
            } else {
                // 获取锁失败,
                acquire(1);
            }
        }

        /**
         * 非公平锁实现,尝试获取锁。
         *
         * @param acquires
         * @return
         */
        @Override
        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }
    }
    /**
     * 采用公平策略获取锁
     */
    static final class FairSync extends Sync {
        private static final long serialVersionUID = -3000897897090466540L;

        @Override
        final void lock() {
            // 以独占模式获取对象，忽略中断
            acquire(1);
        }

        /**
         * tryAcquire 的公平版本。除非递归调用或没有服务或者是第一个，否则不要授予访问权限
         */
        @Override
        protected final boolean tryAcquire(int acquires) {
            // 获取当前线程
            final Thread current = Thread.currentThread();
            // 获取锁的状态
            int c = getState();
            // 通过CAS设置变量State（同步状态）,如果设置成功，也就是获取锁成功，则将当前线程设置为独占线程。
            if (c == 0) {
                // 判断是否有线程正在排队,如果有排队返回true,没有排队尝试获取锁
                if (!hasQueuedPredecessors() &&
                        compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
                // 状态不为0，即资源已经被线程占据,判断是否锁重入
            } else if (current == getExclusiveOwnerThread()) {
                // 下一个状态 + 1
                int nextc = c + acquires;
                if (nextc < 0) {
                    throw new Error("Maximum lock count exceeded");
                }
                setState(nextc);
                return true;
            }
            return false;
        }
    }

    /**
     * 默认支持公平锁
     */
    public MyReentrantLock() {
        sync = new NonfairSync();
    }

    public MyReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }

    @Override
    public void lock() {
        sync.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.nonfairTryAcquire(1);
    }

    @Override
    public boolean tryLock(long timeout, TimeUnit unit)
            throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    public int getHoldCount() {
        return sync.getHoldCount();
    }

    public boolean isHeldByCurrentThread() {
        return sync.isHeldExclusively();
    }

    public boolean isLocked() {
        return sync.isLocked();
    }

    public final boolean isFair() {
        return sync instanceof FairSync;
    }

    protected Thread getOwner() {
        return sync.getOwner();
    }

    public final boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    public final boolean hasQueuedThread(Thread thread) {
        return sync.isQueued(thread);
    }

    public final int getQueueLength() {
        return sync.getQueueLength();
    }

    protected Collection<Thread> getQueuedThreads() {
        return sync.getQueuedThreads();
    }

    public boolean hasWaiters(Condition condition) {
        if (condition == null) {
            throw new NullPointerException();
        }
        if (!(condition instanceof MyAbstractQueuedSynchronizer.ConditionObject)) {
            throw new IllegalArgumentException("not owner");
        }
        return sync.hasWaiters((MyAbstractQueuedSynchronizer.ConditionObject) condition);
    }

    public int getWaitQueueLength(Condition condition) {
        if (condition == null) {
            throw new NullPointerException();
        }
        if (!(condition instanceof MyAbstractQueuedSynchronizer.ConditionObject)) {
            throw new IllegalArgumentException("not owner");
        }
        return sync.getWaitQueueLength((MyAbstractQueuedSynchronizer.ConditionObject) condition);
    }

    protected Collection<Thread> getWaitingThreads(Condition condition) {
        if (condition == null) {
            throw new NullPointerException();
        }
        if (!(condition instanceof MyAbstractQueuedSynchronizer.ConditionObject)) {
            throw new IllegalArgumentException("not owner");
        }
        return sync.getWaitingThreads((MyAbstractQueuedSynchronizer.ConditionObject) condition);
    }

    @Override
    public String toString() {
        Thread o = sync.getOwner();
        return super.toString() + ((o == null) ?
                "[Unlocked]" :
                "[Locked by thread " + o.getName() + "]");
    }
}
