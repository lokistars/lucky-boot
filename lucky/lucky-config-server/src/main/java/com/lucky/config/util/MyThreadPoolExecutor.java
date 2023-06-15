package com.lucky.config.util;

/*import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;*/

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于1.8版本
 * new CustomizableThreadFactory() Spring的线程工厂
 *
 * @author: Loki
 * @data: 2022-05-30 08:51
 */
public class MyThreadPoolExecutor extends AbstractExecutorService {
    /**
     * 当前有效的线程数  int类型变量一共有32位,线程五种状态renState至少需要三位来表示,
     * 所以workerCount只能有29位。 默认 -536870912
     */
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    /**
     * 32-3=29，线程数量所占位数
     */
    private static final int COUNT_BITS = Integer.SIZE - 3;
    /**
     * 低29位表示最大线程数，229-1 536870911
     */
    private static final int CAPACITY = (1 << COUNT_BITS) - 1;

    /**
     * runState is stored in the high-order bits 线程池的五种状态
     * RUNNING:int型变量高3位（含符号位）101 接收新任务和进程队列任务
     * SHUTDOWN:高3位000   不接受新任务,但是接受进程队列任务
     * STOP: 高3位001   不接受新任务也不接受进程队列任务,并且打断正在进行中的任务
     * TIDYING: 高3位010   所有任务终止,待处理任务数量为0,线程转为为TIDYING,将会执行terminated函数
     * TERMINATED: 高3位011  terminated()执行完成
     */

    /**
     * 接收新任务和进程队列任务 -536870912
     */
    private static final int RUNNING = -1 << COUNT_BITS;
    /**
     * 不接受新任务,但是接受进程队列任务 0
     */
    private static final int SHUTDOWN = 0 << COUNT_BITS;
    /**
     * 不接受新任务也不接受进程队列任务,并且打断正在进行中的任务
     * 536870912
     */
    private static final int STOP = 1 << COUNT_BITS;
    /**
     * 所有任务终止,待处理任务数量为0,线程转为为TIDYING,将会执行terminated函数
     * 1073741824
     */
    private static final int TIDYING = 2 << COUNT_BITS;
    /**
     * terminated()执行完成 1610612736
     */
    private static final int TERMINATED = 3 << COUNT_BITS;

    /**
     * 获取当前线程池的状态(前三位) c & -536870912
     * 只有当前线程 C的高三位变化才不会是RUNNING状态
     */
    private static int runStateOf(int c) {
        return c & ~CAPACITY;
    }

    /**
     * 获取当前线程池中线程数
     * CAPACITY 线程总数固定：536870911
     *
     * @param c 当前有效线程数,默认-536870912 新线程递增
     * @return
     */
    private static int workerCountOf(int c) {
        return c & CAPACITY;
    }

    /**
     * 更新线程状态和数量
     *
     * @param rs 当前运行线程数
     * @param wc workerCount 工作线程数
     * @return
     */
    private static int ctlOf(int rs, int wc) {
        return rs | wc;
    }


    private static boolean runStateLessThan(int c, int s) {
        return c < s;
    }

    private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

    /**
     * 是否运行状态
     */
    private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

    /**
     * 尝试对 ctl 的 workerCount 字段进行 CAS 递增
     */
    private boolean compareAndIncrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect + 1);
    }

    /**
     * 尝试对 ctl 的 workerCount 字段进行 CAS 递减
     */
    private boolean compareAndDecrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect - 1);
    }

    /**
     * 递减 ctl 的 workerCount 字段。这仅在线程突然终止时调用（参见 processWorkerExit）。其他减量在 getTask 中执行
     */
    private void decrementWorkerCount() {
        do {
        } while (!compareAndDecrementWorkerCount(ctl.get()));
    }

    /**
     * 用于保存任务并移交给工作线程的队列。我们并不要求 workQueue.poll() 返回 null
     * 一定意味着 workQueue.isEmpty()，所以完全依赖 isEmpty 来查看队列是否为空
     * (例如在决定是否从 SHUTDOWN 过渡到 TIDYING 时必须这样做) .这适用于特殊用途的队列，
     * 例如 DelayQueues，其中 poll() 允许返回 null，即使它稍后可能在延迟到期时返回非 null
     */
    private final BlockingQueue<Runnable> workQueue;

    /**
     * 保护下面的workers,访问workers必须获取这个锁
     */
    private final ReentrantLock mainLock = new ReentrantLock();

    /**
     * 包含池中所有工作线程的集合。仅在持有 mainLock 时访问
     */
    private final HashSet<Worker> workers = new HashSet<Worker>();

    /**
     * 支持 awaitTermination 的等待条件
     */
    private final Condition termination = mainLock.newCondition();

    /**
     * 历史达到的worker数最大值
     */
    private int largestPoolSize;

    /**
     * 已完成任务的计数器。仅在工作线程终止时更新。只能在 mainLock 下访问
     */
    private long completedTaskCount;
    /**
     * 线程工厂 给线程创建对象
     */
    private volatile ThreadFactory threadFactory;
    /**
     * 拒绝策略
     */
    private volatile MyRejectedExecutionHandler handler;
    /**
     * 生存时间- 针对救急线程
     */
    private volatile long keepAliveTime;
    /**
     * true 允许核心线程超时后可以关闭
     */
    private volatile boolean allowCoreThreadTimeOut;
    /**
     * 核心线程数目 (最多保留的线程数)
     */
    private volatile int corePoolSize;
    /**
     * 最大线程数目 = 核心线程+救急线程
     */
    private volatile int maximumPoolSize;

    /**
     * 拒绝策略,默认四种AbortPolicy、CallerRunsPolicy、DiscardPolicy、DiscardOldestPolicy,
     * 建议自己实现,增加监控指标.
     */
    private static final MyRejectedExecutionHandler defaultHandler = new AbortPolicy();

    private static final RuntimePermission shutdownPerm = new RuntimePermission("modifyThread");

    // private final AccessControlContext acc;


    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), defaultHandler);
    }

    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, defaultHandler);
    }

    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, MyRejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), handler);
    }

    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, MyRejectedExecutionHandler handler) {
        if (corePoolSize < 0 || maximumPoolSize <= 0 || maximumPoolSize < corePoolSize || keepAliveTime < 0) {
            throw new IllegalArgumentException();
        }
        if (workQueue == null || threadFactory == null || handler == null) {
            throw new NullPointerException();
        }
        // this.acc = System.getSecurityManager() == null ? null : AccessController.getContext();
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }

    private final class Worker extends MyAbstractQueuedSynchronizer implements Runnable {

        private static final long serialVersionUID = 6138294804551838833L;
        final Thread thread;
        // 当前线程
        Runnable firstTask;
        volatile long completedTasks;

        Worker(Runnable firstTask) {
            // 设置同步状态,不允许中断
            setState(-1); // inhibit interrupts until runWorker
            this.firstTask = firstTask;
            // 通过线程工厂获取线程对象,并设置非守护线程
            this.thread = getThreadFactory().newThread(this);
        }

        @Override
        public void run() {
            runWorker(this);
        }

        @Override
        protected boolean isHeldExclusively() {
            return getState() != 0;
        }

        @Override
        protected boolean tryAcquire(int unused) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public void lock() {
            acquire(1);
        }

        public boolean tryLock() {
            return tryAcquire(1);
        }

        public void unlock() {
            release(1);
        }

        public boolean isLocked() {
            return isHeldExclusively();
        }

        void interruptIfStarted() {
            Thread t;
            if (getState() >= 0 && (t = thread) != null && !t.isInterrupted()) {
                try {
                    t.interrupt();
                } catch (SecurityException ignore) {
                }
            }
        }


    }

    private void advanceRunState(int targetState) {
        for (; ; ) {
            int c = ctl.get();
            if (runStateAtLeast(c, targetState) || ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c)))) {
                break;
            }
        }
    }

    /**
     * 尝试终止线程池
     */
    final void tryTerminate() {
        for (; ; ) {
            int c = ctl.get();
            // 判断线程是否运行状态,判断当前线程池是否已被中断,判断当前线程池是否SHUTDOWN状态并且队列为空
            if (isRunning(c) || runStateAtLeast(c, TIDYING) || (runStateOf(c) == SHUTDOWN && !workQueue.isEmpty())) {
                return;
            }
            // 判断当前线程数不是空的,
            if (workerCountOf(c) != 0) { // Eligible to terminate
                // 循环遍历Workers 判断线程是否有被中断
                // ONLY_ONE:这里只需要中断1个线程去处理shutdown信号就可以了
                interruptIdleWorkers(ONLY_ONE);
                return;
            }

            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                // 线程进入TIDYING状态,所有任务终止,待处理任务数量为0
                if (ctl.compareAndSet(c, ctlOf(TIDYING, 0))) {
                    try {
                        terminated();
                    } finally {
                        // 线程进入TERMINATED状态
                        ctl.set(ctlOf(TERMINATED, 0));
                        termination.signalAll();
                    }
                    return;
                }
            } finally {
                mainLock.unlock();
            }
            // else retry on failed CAS
        }
    }

    private void checkShutdownAccess() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(shutdownPerm);
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                for (Worker w : workers) {
                    security.checkAccess(w.thread);
                }
            } finally {
                mainLock.unlock();
            }
        }
    }

    private void interruptWorkers() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers) {
                w.interruptIfStarted();
            }
        } finally {
            mainLock.unlock();
        }
    }

    private void interruptIdleWorkers(boolean onlyOne) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers) {
                Thread t = w.thread;
                if (!t.isInterrupted() && w.tryLock()) {
                    try {
                        t.interrupt();
                    } catch (SecurityException ignore) {
                    } finally {
                        w.unlock();
                    }
                }
                if (onlyOne) {
                    break;
                }
            }
        } finally {
            mainLock.unlock();
        }
    }

    private void interruptIdleWorkers() {
        interruptIdleWorkers(false);
    }

    private static final boolean ONLY_ONE = true;

    final void reject(Runnable command) {
        //handler.rejectedExecution(command, this);
    }

    void onShutdown() {
    }

    final boolean isRunningOrShutdown(boolean shutdownOK) {
        int rs = runStateOf(ctl.get());
        return rs == RUNNING || (rs == SHUTDOWN && shutdownOK);
    }

    private List<Runnable> drainQueue() {
        BlockingQueue<Runnable> q = workQueue;
        ArrayList<Runnable> taskList = new ArrayList<Runnable>();
        q.drainTo(taskList);
        if (!q.isEmpty()) {
            for (Runnable r : q.toArray(new Runnable[0])) {
                if (q.remove(r)) {
                    taskList.add(r);
                }
            }
        }
        return taskList;
    }

    /**
     * 创建工作线程执行任务
     *
     * @param firstTask 新增一个线程并执行这个任务，可空，增加的线程从队列获取任务
     * @param core      true 核心线程, false 急救线程
     * @return
     */
    private boolean addWorker(Runnable firstTask, boolean core) {
        // 双层循环,通过CAS尝试增加worker线程数量
        retry:
        for (; ; ) {
            //当前有效的线程数
            int c = ctl.get();
            // 获取当前运行线程池状态
            int rs = runStateOf(c);

            // 如果线程池状态是SHUTDOWN、STOP、TIDYING、TERMINATED就不允许提交。
            // && 后面的特殊情况，线程池的状态是SHUTDOWN并且要要执行的任务为Null并且队列不是空，这种情况下是允许增加一个线程来帮助队列中的任务跑完的
            // 因为shutdown状态下，允许执行完成阻塞队里中的任务
            if (rs >= SHUTDOWN && !(rs == SHUTDOWN && firstTask == null && !workQueue.isEmpty())) {
                return false;
            }
            // 主要是通过CAS的方式增加worker的数量
            for (; ; ) {
                // 获取当前线程数
                int wc = workerCountOf(c);
                // 是否超过当前约定的最大值，超过拒绝加入,core true 表示核心线程数
                if (wc >= CAPACITY || wc >= (core ? corePoolSize : maximumPoolSize)) {
                    return false;
                }
                //没超过约定值，那么通过CAS的方式增加worker的数量，增加成功就跳出外层循环
                if (compareAndIncrementWorkerCount(c)) {
                    break retry;
                }
                c = ctl.get();  // Re-read ctl
                // 获取当前线程状态,判断当前运行状态是不是改变了
                if (runStateOf(c) != rs) {
                    //外层循环重新执行
                    continue retry;
                    // else CAS failed due to workerCount change; retry inner loop
                }
            }
        }
        // 线程是否启动成功
        boolean workerStarted = false;
        // true 表示工作线程添加成功
        boolean workerAdded = false;
        Worker w = null;
        try {
            //构建worker，并将当前任务赋值给当前worker,这个地方要看一下new Worker的源码，
            //你会发现会直接new一个线程给当前worker对象,非守护线程
            w = new Worker(firstTask);
            final Thread t = w.thread;
            if (t != null) {
                // 获取锁对象
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    //保持锁定时重新检查线程池状态
                    int rs = runStateOf(ctl.get());
                    // 如果线程是RUNNING状态,或者线程是SHUTDOWN切任务是空的 加入workers队列中
                    if (rs < SHUTDOWN || (rs == SHUTDOWN && firstTask == null)) {
                        // 检查当前线程是不是已经开始跑了
                        if (t.isAlive()) {
                            throw new IllegalThreadStateException();
                        }
                        // 添加到workers队列中。
                        workers.add(w);
                        int s = workers.size();
                        // 判断当前队列中线程数是否达到之前历史最大线程数。
                        if (s > largestPoolSize) {
                            largestPoolSize = s;
                        }
                        // 工作线程添加成功
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                // 工作线程队列添加成功后启动线程
                if (workerAdded) {
                    // 启动线程,会执行Worker中的run方法,实际runWorker(this)
                    t.start();
                    workerStarted = true;
                }
            }
        } finally {
            // 线程启动失败,删除workers队列中的线程
            if (!workerStarted) {
                addWorkerFailed(w);
            }
        }
        return workerStarted;
    }

    private void addWorkerFailed(Worker w) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if (w != null) {
                workers.remove(w);
            }
            // 通过cas,将工作线程数减一
            decrementWorkerCount();
            // 尝试终止线程池
            tryTerminate();
        } finally {
            mainLock.unlock();
        }
    }

    private void processWorkerExit(Worker w, boolean completedAbruptly) {
        if (completedAbruptly) // If abrupt, then workerCount wasn't adjusted
        {
            decrementWorkerCount();
        }

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            completedTaskCount += w.completedTasks;
            workers.remove(w);
        } finally {
            mainLock.unlock();
        }
        // 尝试终止线程池
        tryTerminate();

        int c = ctl.get();
        if (runStateLessThan(c, STOP)) {
            if (!completedAbruptly) {
                int min = allowCoreThreadTimeOut ? 0 : corePoolSize;
                if (min == 0 && !workQueue.isEmpty()) {
                    min = 1;
                }
                if (workerCountOf(c) >= min) {
                    return; // replacement not needed
                }
            }
            addWorker(null, false);
        }
    }

    /**
     * 从队列中获取可执行任务
     *
     * @return
     */
    private Runnable getTask() {
        // 获取任务是否超时
        boolean timedOut = false;

        for (; ; ) {
            // 获取当前任务数
            int c = ctl.get();
            // 获取当前线程池的状态
            int rs = runStateOf(c);

            // 判断线程池状态,不会处理队列中的任务了。
            if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
                // 如果当前线程池是中断状态,或任务对象是空的,workerCount进行递减,循环清空
                decrementWorkerCount();
                return null;
            }
            // 获取当前线程池中线程数量
            int wc = workerCountOf(c);

            // 标记从队列中取任务时是否设置超时时间,
            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;
            // 当前线程数是否大于最大线程数或核心线程数是否超时
            // 当前线程数大于1并且任务队列为空
            if ((wc > maximumPoolSize || (timed && timedOut)) && (wc > 1 || workQueue.isEmpty())) {
                // workerCount递减，结束当前thread
                if (compareAndDecrementWorkerCount(c)) {
                    return null;
                }
                continue;
            }

            try {
                //以指定的超时时间从队列中取任务,没有超时阻塞获取任务
                Runnable r = timed ? workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) : workQueue.take();
                // 返回任务
                if (r != null) {
                    return r;
                }
                // 获取任务超时
                timedOut = true;
            } catch (InterruptedException retry) {
                // 线程被中断重试
                timedOut = false;
            }
        }
    }

    final void runWorker(Worker w) {
        //获取当前工作线程
        Thread wt = Thread.currentThread();
        // 获取任务
        Runnable task = w.firstTask;
        w.firstTask = null;
        // Worker构造中抑制了线程中断,在这里允许中断
        w.unlock(); // allow interrupts
        // 用于标识是否异常终止,为true的情况：1.执行任务抛出异常；2.被中断
        boolean completedAbruptly = true;
        try {
            // 判断任务是否为空,存在addWorker(null)情况
            // 如果task为空,那么getTask会将workerCount递减,如果异常了这个递减操作会在processWorkerExit中处理
            while (task != null || (task = getTask()) != null) {
                w.lock();
                // If pool is stopping, ensure thread is interrupted;
                // if not, ensure thread is not interrupted.  This
                // requires a recheck in second case to deal with
                // shutdownNow race while clearing interrupt
                if ((runStateAtLeast(ctl.get(), STOP) || (Thread.interrupted() && runStateAtLeast(ctl.get(), STOP))) && !wt.isInterrupted()) {
                    wt.interrupt();
                }
                try {
                    // 执行任务前可以插入一些处理,子类重载
                    beforeExecute(wt, task);
                    Throwable thrown = null;
                    try {
                        // 执行用户任务
                        task.run();
                    } catch (RuntimeException x) {
                        thrown = x;
                        throw x;
                    } catch (Error x) {
                        thrown = x;
                        throw x;
                    } catch (Throwable x) {
                        thrown = x;
                        throw new Error(x);
                    } finally {
                        // 执行任务后可以插入一些处理,子类重载
                        afterExecute(task, thrown);
                    }
                } finally {
                    // 当前任务执行完毕
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            // 结束线程的一些清理工作
            processWorkerExit(w, completedAbruptly);
        }
    }

    /**
     * @param command
     */
    @Override
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }
        //获取到当前有效的线程数和线程池的状态
        int c = ctl.get();
        // 获取当前正在运行线程数是否小于核心线程池，是则新创建一个线程执行任务，否则将任务放到任务队列中
        if (workerCountOf(c) < corePoolSize) {
            //在addWorker中创建工作线程执行任务 ，true 标志为核心线程
            if (addWorker(command, true)) {
                // 增加worker成功且worker跑起来了就返回
                return;
            }
            // 线程池拒绝了增加worker, 重新获取线程池状态
            c = ctl.get();
        }
        // worker增加失败,或者当前线程池中线程数超过核心线程数,此时将线程放到任务队列中
        // 线程池是否处于运行状态，且是否任务插入任务队列成功
        if (isRunning(c) && workQueue.offer(command)) {
            // 重新获取线程状态
            int recheck = ctl.get();
            //线程池是否处于运行状态，如果不是则删除队列中的任务
            if (!isRunning(recheck) && remove(command)) {
                //执行拒绝策略
                reject(command);
                //查看当前工作线程的数量
            } else if (workerCountOf(recheck) == 0) {
                //如果当前线程数是0,那么刚刚的任务肯定在阻塞队列里面了,这个时候开启一个没有任务的线程去跑.
                //防止SHUTDOWN状态下没有活动线程了，但是队列里还有任务没执行这种特殊情况
                addWorker(null, false);
            }
            // 插入队列不成功，且当前线程数数量小于最大线程池数量，此时则创建新线程执行任务，创建失败执行拒绝策略
        } else if (!addWorker(command, false)) {
            reject(command);
        }
    }

    @Override
    public void shutdown() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            advanceRunState(SHUTDOWN);
            interruptIdleWorkers();
            onShutdown(); // hook for ScheduledThreadPoolExecutor
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
    }


    @Override
    public List<Runnable> shutdownNow() {
        List<Runnable> tasks;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            // STOP状态：不再接受新任务且不再执行队列中的任务。
            advanceRunState(STOP);
            // 中断所有线程
            interruptWorkers();
            // 返回队列中还没有被执行的任务。
            tasks = drainQueue();
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
        return tasks;
    }

    @Override
    public boolean isShutdown() {
        return !isRunning(ctl.get());
    }

    public boolean isTerminating() {
        int c = ctl.get();
        return !isRunning(c) && runStateLessThan(c, TERMINATED);
    }

    @Override
    public boolean isTerminated() {
        return runStateAtLeast(ctl.get(), TERMINATED);
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (; ; ) {
                if (runStateAtLeast(ctl.get(), TERMINATED)) {
                    return true;
                }
                if (nanos <= 0) {
                    return false;
                }
                nanos = termination.awaitNanos(nanos);
            }
        } finally {
            mainLock.unlock();
        }
    }

    @Override
    protected void finalize() {
        shutdown();
        /*SecurityManager sm = System.getSecurityManager();
        if (sm == null || acc == null) {
            shutdown();
        } else {
            PrivilegedAction<Void> pa = () -> {
                shutdown();
                return null;
            };
            AccessController.doPrivileged(pa, acc);
        }*/
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException();
        }
        this.threadFactory = threadFactory;
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setRejectedExecutionHandler(MyRejectedExecutionHandler handler) {
        if (handler == null) {
            throw new NullPointerException();
        }
        this.handler = handler;
    }

    public MyRejectedExecutionHandler getRejectedExecutionHandler() {
        return handler;
    }

    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize < 0) {
            throw new IllegalArgumentException();
        }
        int delta = corePoolSize - this.corePoolSize;
        this.corePoolSize = corePoolSize;
        if (workerCountOf(ctl.get()) > corePoolSize) {
            interruptIdleWorkers();
        } else if (delta > 0) {
            // We don't really know how many new threads are "needed".
            // As a heuristic, prestart enough new workers (up to new
            // core size) to handle the current number of tasks in
            // queue, but stop if queue becomes empty while doing so.
            int k = Math.min(delta, workQueue.size());
            while (k-- > 0 && addWorker(null, true)) {
                if (workQueue.isEmpty()) {
                    break;
                }
            }
        }
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public boolean prestartCoreThread() {
        return workerCountOf(ctl.get()) < corePoolSize && addWorker(null, true);
    }

    void ensurePrestart() {
        int wc = workerCountOf(ctl.get());
        if (wc < corePoolSize) {
            addWorker(null, true);
        } else if (wc == 0) {
            addWorker(null, false);
        }
    }

    public int prestartAllCoreThreads() {
        int n = 0;
        while (addWorker(null, true)) {
            ++n;
        }
        return n;
    }

    public boolean allowsCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }

    public void allowCoreThreadTimeOut(boolean value) {
        if (value && keepAliveTime <= 0) {
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        }
        if (value != allowCoreThreadTimeOut) {
            allowCoreThreadTimeOut = value;
            if (value) {
                interruptIdleWorkers();
            }
        }
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0 || maximumPoolSize < corePoolSize) {
            throw new IllegalArgumentException();
        }
        this.maximumPoolSize = maximumPoolSize;
        if (workerCountOf(ctl.get()) > maximumPoolSize) {
            interruptIdleWorkers();
        }
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setKeepAliveTime(long time, TimeUnit unit) {
        if (time < 0) {
            throw new IllegalArgumentException();
        }
        if (time == 0 && allowsCoreThreadTimeOut()) {
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        }
        long keepAliveTime = unit.toNanos(time);
        long delta = keepAliveTime - this.keepAliveTime;
        this.keepAliveTime = keepAliveTime;
        if (delta < 0) {
            interruptIdleWorkers();
        }
    }

    public long getKeepAliveTime(TimeUnit unit) {
        return unit.convert(keepAliveTime, TimeUnit.NANOSECONDS);
    }

    public BlockingQueue<Runnable> getQueue() {
        return workQueue;
    }

    public boolean remove(Runnable task) {
        boolean removed = workQueue.remove(task);
        tryTerminate(); // In case SHUTDOWN and now empty
        return removed;
    }

    public void purge() {
        final BlockingQueue<Runnable> q = workQueue;
        try {
            Iterator<Runnable> it = q.iterator();
            while (it.hasNext()) {
                Runnable r = it.next();
                if (r instanceof Future<?> && ((Future<?>) r).isCancelled()) {
                    it.remove();
                }
            }
        } catch (ConcurrentModificationException fallThrough) {
            // Take slow path if we encounter interference during traversal.
            // Make copy for traversal and call remove for cancelled entries.
            // The slow path is more likely to be O(N*N).
            for (Object r : q.toArray()) {
                if (r instanceof Future<?> && ((Future<?>) r).isCancelled()) {
                    q.remove(r);
                }
            }
        }

        tryTerminate(); // In case SHUTDOWN and now empty
    }

    public int getPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            // Remove rare and surprising possibility of
            // isTerminated() && getPoolSize() > 0
            return runStateAtLeast(ctl.get(), TIDYING) ? 0 : workers.size();
        } finally {
            mainLock.unlock();
        }
    }

    public int getActiveCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int n = 0;
            for (Worker w : workers) {
                if (w.isLocked()) {
                    ++n;
                }
            }
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    public int getLargestPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return largestPoolSize;
        } finally {
            mainLock.unlock();
        }
    }

    public long getTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = completedTaskCount;
            for (Worker w : workers) {
                n += w.completedTasks;
                if (w.isLocked()) {
                    ++n;
                }
            }
            return n + workQueue.size();
        } finally {
            mainLock.unlock();
        }
    }


    public long getCompletedTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = completedTaskCount;
            for (Worker w : workers) {
                n += w.completedTasks;
            }
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    @Override
    public String toString() {
        long ncompleted;
        int nworkers, nactive;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            ncompleted = completedTaskCount;
            nactive = 0;
            nworkers = workers.size();
            for (Worker w : workers) {
                ncompleted += w.completedTasks;
                if (w.isLocked()) {
                    ++nactive;
                }
            }
        } finally {
            mainLock.unlock();
        }
        int c = ctl.get();
        String rs = (runStateLessThan(c, SHUTDOWN) ? "Running" : (runStateAtLeast(c, TERMINATED) ? "Terminated" : "Shutting down"));
        return super.toString() + "[" + rs + ", pool size = " + nworkers + ", active threads = " + nactive + ", queued tasks = " + workQueue.size() + ", completed tasks = " + ncompleted + "]";
    }

    protected void beforeExecute(Thread t, Runnable r) {
    }

    protected void afterExecute(Runnable r, Throwable t) {
    }

    protected void terminated() {
    }

    private interface MyRejectedExecutionHandler {
        void rejectedExecution(Runnable r, MyThreadPoolExecutor executor);
    }

    public static class CallerRunsPolicy implements MyRejectedExecutionHandler {
        /**
         * Creates a {@code CallerRunsPolicy}.
         */
        public CallerRunsPolicy() {
        }

        /**
         * Executes task r in the caller's thread, unless the executor
         * has been shut down, in which case the task is discarded.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         */
        @Override
        public void rejectedExecution(Runnable r, MyThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                r.run();
            }
        }
    }


    public static class AbortPolicy implements MyRejectedExecutionHandler {
        /**
         * Creates an {@code AbortPolicy}.
         */
        public AbortPolicy() {
        }

        /**
         * Always throws RejectedExecutionException.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         * @throws RejectedExecutionException always
         */
        @Override
        public void rejectedExecution(Runnable r, MyThreadPoolExecutor e) {
            throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + e.toString());
        }
    }

    public static class DiscardPolicy implements MyRejectedExecutionHandler {
        /**
         * Creates a {@code DiscardPolicy}.
         */
        public DiscardPolicy() {
        }

        /**
         * Does nothing, which has the effect of discarding task r.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         */
        @Override
        public void rejectedExecution(Runnable r, MyThreadPoolExecutor e) {

        }
    }

    public static class DiscardOldestPolicy implements MyRejectedExecutionHandler {
        /**
         * Creates a {@code DiscardOldestPolicy} for the given executor.
         */
        public DiscardOldestPolicy() {
        }

        /**
         * Obtains and ignores the next task that the executor
         * would otherwise execute, if one is immediately available,
         * and then retries execution of task r, unless the executor
         * is shut down, in which case task r is instead discarded.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         */
        @Override
        public void rejectedExecution(Runnable r, MyThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                e.getQueue().poll();
                e.execute(r);
            }
        }
    }
}
