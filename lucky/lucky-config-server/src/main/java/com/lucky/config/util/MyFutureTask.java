package com.lucky.config.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

/**
 * @program: lucky
 * @description: 基于1.8源码研究
 * @author: Loki
 * @data: 2023-06-23 11:49
 **/
public class MyFutureTask<V> implements RunnableFuture<V> {

    private static final int NEW = 0; // 新建任务
    private static final int COMPLETING = 1;    // callable的结果,正在封装给当前的Future
    private static final int NORMAL = 2;        //NORMAL 任务正常结束
    private static final int EXCEPTIONAL = 3;  // 执行任务时发生了异常
    private static final int CANCELLED = 4;    // 任务被取消了
    private static final int INTERRUPTING = 5; // 线程被中断状态
    private static final int INTERRUPTED = 6; // 线程被中断了
    // VarHandle mechanics
    private static final VarHandle STATE;
    private static final VarHandle RUNNER;
    private static final VarHandle WAITERS;

    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            STATE = l.findVarHandle(MyFutureTask.class, "state", int.class);
            RUNNER = l.findVarHandle(MyFutureTask.class, "runner", Thread.class);
            WAITERS = l.findVarHandle(MyFutureTask.class, "waiters", MyFutureTask.WaitNode.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }

        // Reduce the risk of rare disastrous classloading in first call to
        // LockSupport.park: https://bugs.openjdk.java.net/browse/JDK-8074773
        Class<?> ensureLoaded = LockSupport.class;
    }

    /**
     * 当前任务在运行过程中的状态
     */
    private volatile int state;
    /**
     * 当前要执行的任务
     */
    private Callable<V> callable;
    /**
     * 存放返回的结果,从get()方法获取
     */
    private Object outcome;
    /**
     * 运行可调用的线程
     */
    private volatile Thread runner;
    /**
     * 单向链表,存放通过get方法挂起等待的线程。
     */
    private volatile MyFutureTask.WaitNode waiters;

    public MyFutureTask(Callable<V> callable) {
        if (callable == null) {
            throw new NullPointerException();
        }
        this.callable = callable;
        this.state = NEW;
    }

    public MyFutureTask(Runnable runnable, V result) {
        this.callable = Executors.callable(runnable, result);
        this.state = NEW;
    }

    private V report(int s) throws ExecutionException {
        Object x = outcome;
        if (s == NORMAL) {
            return (V) x;
        }
        if (s >= CANCELLED) {
            throw new CancellationException();
        }
        throw new ExecutionException((Throwable) x);
    }


    /**
     * 外部线程可以通过该方法调用取消任务的执行。
     * @param mayInterruptIfRunning {@code true} if the thread
     * executing this task should be interrupted (if the thread is
     * known to the implementation); otherwise, in-progress tasks are
     * allowed to complete
     * @return
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        //
        if (!(state == NEW && STATE.compareAndSet
                (this, NEW, mayInterruptIfRunning ? INTERRUPTING : CANCELLED))) {
            return false;
        }
        try {
            if (mayInterruptIfRunning) {
                try {
                    Thread t = runner;
                    if (t != null) {
                        t.interrupt();
                    }
                } finally { // final state
                    STATE.setRelease(this, INTERRUPTED);
                }
            }
        } finally {
            finishCompletion();
        }
        return true;
    }

    @Override
    public boolean isCancelled() {
        return state >= CANCELLED;
    }

    @Override
    public boolean isDone() {
        return state != NEW;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        int s = state;
        if (s <= COMPLETING) {
            s = awaitDone(false, 0L);
        }
        return report(s);
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (unit == null) {
            throw new NullPointerException();
        }
        int s = state;
        if (s <= COMPLETING &&
                (s = awaitDone(true, unit.toNanos(timeout))) <= COMPLETING) {
            throw new TimeoutException();
        }
        return report(s);
    }

    protected void done() {
    }

    protected void set(V v) {
        // 将任务状态从NEW设置为COMPLETING
        if (STATE.compareAndSet(this, NEW, COMPLETING)) {
            outcome = v;
            // 修改为NORMAL 表示任务正常结束
            STATE.setRelease(this, NORMAL); // final state
            finishCompletion();
        }
    }

    /**
     * 当任务执行时发生了异常,那么调用该方法,将FutureTask变为异常完成状态。
     * @param t 异常
     */
    protected void setException(Throwable t) {
        if (STATE.compareAndSet(this, NEW, COMPLETING)) {
            outcome = t;
            STATE.setRelease(this, EXCEPTIONAL); // final state
            finishCompletion();
        }
    }

    @Override
    public void run() {
        // 状态必须是新建状态,且成功的将runner修改为当前线程,futureTask保留了当前正在执行他的线程,所以可以通过runner对象,在cancel中断线程。
        if (state != NEW ||
                !RUNNER.compareAndSet(this, null, Thread.currentThread())) {
            return;
        }
        try {
            Callable<V> c = callable;
            // 查看内部执行的执行体,也就是用户定义的callable是否为空,切当前状态是否被改变,即执行前状态必须是NEW
            if (c != null && state == NEW) {
                V result;
                boolean ran;
                try {
                    // // 执行用户定义的函数
                    result = c.call();
                    ran = true;
                } catch (Throwable ex) {
                    result = null;
                    ran = false;
                    //设置执行失败的结果
                    setException(ex);
                }
                //获取返回结果outcome
                if (ran) {
                    set(result);
                }
            }
        } finally {
            //执行完毕后,不持有thread的引用
            runner = null;
            // 判断状态是否被中断,如果是中断处理，调用下面的函数
            int s = state;
            if (s >= INTERRUPTING) {
                handlePossibleCancellationInterrupt(s);
            }
        }
    }

    protected boolean runAndReset() {
        if (state != NEW ||
                !RUNNER.compareAndSet(this, null, Thread.currentThread())) {
            return false;
        }
        boolean ran = false;
        int s = state;
        try {
            Callable<V> c = callable;
            if (c != null && s == NEW) {
                try {
                    c.call(); // don't set result
                    ran = true;
                } catch (Throwable ex) {
                    setException(ex);
                }
            }
        } finally {
            // runner must be non-null until state is settled to
            // prevent concurrent calls to run()
            runner = null;
            // state must be re-read after nulling runner to prevent
            // leaked interrupts
            s = state;
            if (s >= INTERRUPTING) {
                handlePossibleCancellationInterrupt(s);
            }
        }
        return ran && s == NEW;
    }

    private void handlePossibleCancellationInterrupt(int s) {
        // It is possible for our interrupter to stall before getting a
        // chance to interrupt us.  Let's spin-wait patiently.
        if (s == INTERRUPTING) {
            while (state == INTERRUPTING) {
                Thread.yield(); // wait out pending interrupt
            }
        }

        // assert state == INTERRUPTED;

        // We want to clear any interrupt we may have received from
        // cancel(true).  However, it is permissible to use interrupts
        // as an independent mechanism for a task to communicate with
        // its caller, and there is no way to clear only the
        // cancellation interrupt.
        //
        // Thread.interrupted();
    }

    private void finishCompletion() {
        // assert state > COMPLETING;
        for (MyFutureTask.WaitNode q; (q = waiters) != null; ) {
            if (WAITERS.weakCompareAndSet(this, q, null)) {
                for (; ; ) {
                    Thread t = q.thread;
                    if (t != null) {
                        q.thread = null;
                        LockSupport.unpark(t);
                    }
                    MyFutureTask.WaitNode next = q.next;
                    if (next == null)
                        break;
                    q.next = null; // unlink to help gc
                    q = next;
                }
                break;
            }
        }

        done();

        callable = null;        // to reduce footprint
    }

    private int awaitDone(boolean timed, long nanos)
            throws InterruptedException {
        // The code below is very delicate, to achieve these goals:
        // - call nanoTime exactly once for each call to park
        // - if nanos <= 0L, return promptly without allocation or nanoTime
        // - if nanos == Long.MIN_VALUE, don't underflow
        // - if nanos == Long.MAX_VALUE, and nanoTime is non-monotonic
        //   and we suffer a spurious wakeup, we will do no worse than
        //   to park-spin for a while
        long startTime = 0L;    // Special value 0L means not yet parked
        MyFutureTask.WaitNode q = null;
        boolean queued = false;
        for (; ; ) {
            int s = state;
            if (s > COMPLETING) {
                if (q != null) {
                    q.thread = null;
                }
                return s;
            } else if (s == COMPLETING) {
                // We may have already promised (via isDone) that we are done
                // so never return empty-handed or throw InterruptedException
                Thread.yield();
            } else if (Thread.interrupted()) {
                removeWaiter(q);
                throw new InterruptedException();
            } else if (q == null) {
                if (timed && nanos <= 0L) {
                    return s;
                }
                q = new MyFutureTask.WaitNode();
            } else if (!queued) {
                queued = WAITERS.weakCompareAndSet(this, q.next = waiters, q);
            } else if (timed) {
                final long parkNanos;
                if (startTime == 0L) { // first time
                    startTime = System.nanoTime();
                    if (startTime == 0L) {
                        startTime = 1L;
                    }
                    parkNanos = nanos;
                } else {
                    long elapsed = System.nanoTime() - startTime;
                    if (elapsed >= nanos) {
                        removeWaiter(q);
                        return state;
                    }
                    parkNanos = nanos - elapsed;
                }
                // nanoTime may be slow; recheck before parking
                if (state < COMPLETING) {
                    LockSupport.parkNanos(this, parkNanos);
                }
            } else {
                LockSupport.park(this);
            }
        }
    }

    private void removeWaiter(MyFutureTask.WaitNode node) {
        if (node != null) {
            node.thread = null;
            retry:
            for (; ; ) {          // restart on removeWaiter race
                for (MyFutureTask.WaitNode pred = null, q = waiters, s; q != null; q = s) {
                    s = q.next;
                    if (q.thread != null) {
                        pred = q;
                    } else if (pred != null) {
                        pred.next = s;
                        if (pred.thread == null) {
                            // check for race
                            continue retry;
                        }
                    } else if (!WAITERS.compareAndSet(this, q, s)) {
                        continue retry;
                    }
                }
                break;
            }
        }
    }

    public String toString() {
        final String status;
        switch (state) {
            case NORMAL:
                status = "[Completed normally]";
                break;
            case EXCEPTIONAL:
                status = "[Completed exceptionally: " + outcome + "]";
                break;
            case CANCELLED:
            case INTERRUPTING:
            case INTERRUPTED:
                status = "[Cancelled]";
                break;
            default:
                final Callable<?> callable = this.callable;
                status = (callable == null)
                        ? "[Not completed]"
                        : "[Not completed, task = " + callable + "]";
        }
        return super.toString() + status;
    }

    static final class WaitNode {
        volatile Thread thread;
        volatile MyFutureTask.WaitNode next;

        WaitNode() {
            thread = Thread.currentThread();
        }
    }


}
