package com.lucky.config.util;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.function.Consumer;

/**
 * 基于1.8
 * 使用单项链表结构实现线程安全阻塞队列,他可以是有界队列,也可以是无界队列。
 * 他的生产和消费使用的不是同一把锁,所以队列的生产和消费是可以同时进行的。
 * @author: Loki
 * @data: 2022-05-30 10:40
 */
public class MyLinkedBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {
    private static final long serialVersionUID = -6903933977591709194L;

    static class Node<E> {
        E item;

        /**
         * One of:
         * - the real successor Node
         * - this Node, meaning the successor is head.next
         * - null, meaning there is no successor (this is the last node)
         */
        Node<E> next;

        Node(E x) {
            item = x;
        }
    }
    // 容量,默认为Integer.MAX_VALUE
    private final int capacity;
    // 元素的个数,使用AtomicInteger是因为生产和消费是两把锁,都有可能修改count,存在线程安全问题。
    private final AtomicInteger count = new AtomicInteger();
    // 头节点
    transient Node<E> head;
    // 尾节点
    private transient Node<E> last;
    // 消费锁
    private final MyReentrantLock takeLock = new MyReentrantLock();
    // 消费线程等待队列
    private final Condition notEmpty = takeLock.newCondition();
    // 生产锁
    private final MyReentrantLock putLock = new MyReentrantLock();
    // 生产线程等待队列
    private final Condition notFull = putLock.newCondition();

    private void signalNotEmpty() {
        final MyReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

    /**
     * 通知生产者线程继续生产
     */
    private void signalNotFull() {
        final MyReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            // 唤醒一个在notEmpty条件队列中等待的消费线程
            notFull.signal();
        } finally {
            putLock.unlock();
        }
    }

    /**
     * 入队
     * @param node
     */
    private void enqueue(Node<E> node) {
        // assert putLock.isHeldByCurrentThread();
        // assert last.next == null;
        last = last.next = node;
    }

    /**
     * 队列头元素出队
     * @return
     */
    private E dequeue() {
        // assert takeLock.isHeldByCurrentThread();
        // assert head.item == null;
        Node<E> h = head;
        Node<E> first = h.next;
        h.next = h; // help GC
        head = first;
        E x = first.item;
        first.item = null;
        return x;
    }

    void fullyLock() {
        putLock.lock();
        takeLock.lock();
    }

    void fullyUnlock() {
        takeLock.unlock();
        putLock.unlock();
    }

    public MyLinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }

    /**
     * 创建有界队列,指定容量大小
     * @param capacity
     */
    public MyLinkedBlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.capacity = capacity;
        // 头尾节点指向一个空的Node节点
        last = head = new Node<E>(null);
    }

    public MyLinkedBlockingQueue(Collection<? extends E> c) {
        this(Integer.MAX_VALUE);
        final MyReentrantLock putLock = this.putLock;
        putLock.lock(); // Never contended, but necessary for visibility
        try {
            int n = 0;
            for (E e : c) {
                if (e == null) {
                    throw new NullPointerException();
                }
                if (n == capacity) {
                    throw new IllegalStateException("Queue full");
                }
                enqueue(new Node<E>(e));
                ++n;
            }
            count.set(n);
        } finally {
            putLock.unlock();
        }
    }

    @Override
    public int size() {
        return count.get();
    }

    @Override
    public int remainingCapacity() {
        return capacity - count.get();
    }

    /**
     * 将元素入队,如果队列满了会一直阻塞,直到操作完成为止
     * @param e 指定元素
     * @throws InterruptedException
     */
    @Override
    public void put(E e) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }
        // Note: convention in all put/take/etc is to preset local var
        // holding count negative to indicate failure unless set.
        int c = -1;
        Node<E> node = new Node<E>(e);
        final MyReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        //可中断的等待获取生产者锁，即响应中断
        putLock.lockInterruptibly();
        try {
            /*
             * Note that count is used in wait guard even though it is
             * not protected by lock. This works because count can
             * only decrease at this point (all other puts are shut
             * out by lock), and we (or some other waiting put) are
             * signalled if it ever changes from capacity. Similarly
             * for all other uses of count in other wait guards.
             * 如果队列满了,阻塞等待其他消费线程 结点数量是否等于容量，即队列是否满了
             */
            while (count.get() == capacity) {
                notFull.await();
            }
            // 入队列 结点添加到链表尾部
            enqueue(node);
            //获取此时计数器的值赋给c，并且计数器值自增1
            c = count.getAndIncrement();
            // 队列没满通知其他线程继续生产
            if (c + 1 < capacity) {
                //唤醒一个在notFull条件队列中等待的生产线程
                notFull.signal();
            }
        } finally {
            putLock.unlock(); //释放生产者锁
        }
        //  如果前面没有抛出异常，那么在finally之后会执行下面的代码
        //  如果c为0，那么此时队列中还可能有存在1条数据，刚放进去的
        //  那么由于刚才队列没有数据，可能此时有消费者线程在等待，这里需要唤醒一个消费者线程
        //  如果此前队列中就有数据没有消费完毕，那么也不必唤醒唤醒消费者
        if (c == 0) {
            //获取消费者锁并且尝试唤醒一个消费者线程
            signalNotEmpty();
        }
    }

    /**
     * 超时等待
     * @param e
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    @Override
    public boolean offer(E e, long timeout, TimeUnit unit)
            throws InterruptedException {

        if (e == null) {
            throw new NullPointerException();
        }
        long nanos = unit.toNanos(timeout);
        int c = -1;
        final MyReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            while (count.get() == capacity) {
                if (nanos <= 0) {
                    return false;
                }
                nanos = notFull.awaitNanos(nanos);
            }
            enqueue(new Node<E>(e));
            c = count.getAndIncrement();
            if (c + 1 < capacity) {
                notFull.signal();
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }
        return true;
    }

    /**
     * 尝试将元素放入队列,如果入队成功返回true，如果队列满返回false
     * @param e 指定元素
     * @return
     */
    @Override
    public boolean offer(E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        final AtomicInteger count = this.count;
        // 校验是否超过容量
        if (count.get() == capacity) {
            return false;
        }
        int c = -1;
        Node<E> node = new Node<E>(e);
        // 加锁
        final MyReentrantLock putLock = this.putLock;
        // 不可中断的等待获取生产者锁，即不响应中断
        putLock.lock();
        try {
            // 校验是否超过容量
            if (count.get() < capacity) {
                // 插入节点的下一个
                enqueue(node);
                // 计数加1
                c = count.getAndIncrement();
                if (c + 1 < capacity) {
                    // 队列还有空间,唤醒其他线程
                    notFull.signal();
                }
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0) {
            // 数量从0变成1,需要通知消费者线程消费。
            signalNotEmpty();
        }
        return c >= 0;
    }

    /**
     * 当队列中没有元素时会一直阻塞,直到取出数据
     * @return
     * @throws InterruptedException
     */
    @Override
    public E take() throws InterruptedException {
        E x;
        int c = -1;
        final AtomicInteger count = this.count;
        final MyReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            // 队列空的,阻塞等待生产数据 挂起线程并且释放锁
            while (count.get() == 0) {
                notEmpty.await();
            }
            // 队列头元素出队
            x = dequeue();
            c = count.getAndDecrement();
            if (c > 1) {
                notEmpty.signal();
            }
        } finally {
            takeLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }
        return x;
    }

    /**
     *
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E x = null;
        int c = -1;
        // 获取时间
        long nanos = unit.toNanos(timeout);
        final AtomicInteger count = this.count;
        final MyReentrantLock takeLock = this.takeLock;
        // 加锁
        takeLock.lockInterruptibly();
        try {
            while (count.get() == 0) {
                if (nanos <= 0) {
                    return null;
                }
                // 唤醒线程
                nanos = notEmpty.awaitNanos(nanos);
            }
            x = dequeue();
            c = count.getAndDecrement();
            if (c > 1) {
                notEmpty.signal();
            }
        } finally {
            takeLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }
        return x;
    }

    /**
     * 返回并移除链头元素
     * @return
     */
    @Override
    public E poll() {
        final AtomicInteger count = this.count;
        if (count.get() == 0) {
            return null;
        }
        E x = null;
        int c = -1;
        final MyReentrantLock takeLock = this.takeLock;
        // 竞争消费锁
        takeLock.lock();
        try {
            if (count.get() > 0) {
                // 链表头元素出队列
                x = dequeue();
                // 总数递减
                c = count.getAndDecrement();
                if (c > 1) {
                    // 只要队列没空就通知其他消费线程继续消费
                    notEmpty.signal();
                }
            }
        } finally {
            takeLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }
        return x;
    }

    @Override
    public E peek() {
        if (count.get() == 0) {
            return null;
        }
        final MyReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            Node<E> first = head.next;
            if (first == null) {
                return null;
            } else {
                return first.item;
            }
        } finally {
            takeLock.unlock();
        }
    }

    void unlink(Node<E> p, Node<E> trail) {
        // assert isFullyLocked();
        // p.next is not changed, to allow iterators that are
        // traversing p to maintain their weak-consistency guarantee.
        p.item = null;
        trail.next = p.next;
        if (last == p) {
            last = trail;
        }
        if (count.getAndDecrement() == capacity) {
            notFull.signal();
        }
    }


    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }

        fullyLock();

        try {
            for (Node<E> p = head.next; p != null; p = p.next) {
                if (o.equals(p.item)) {
                    return true;
                }
            }
            return false;
        } finally {
            fullyUnlock();
        }
    }

    @Override
    public Object[] toArray() {
        fullyLock();
        try {
            int size = count.get();
            Object[] a = new Object[size];
            int k = 0;
            for (Node<E> p = head.next; p != null; p = p.next) {
                a[k++] = p.item;
            }
            return a;
        } finally {
            fullyUnlock();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        fullyLock();
        try {
            int size = count.get();
            if (a.length < size) {
                a = (T[]) java.lang.reflect.Array.newInstance
                        (a.getClass().getComponentType(), size);
            }

            int k = 0;
            for (Node<E> p = head.next; p != null; p = p.next) {
                a[k++] = (T) p.item;
            }

            if (a.length > k) {
                a[k] = null;
            }
            return a;
        } finally {
            fullyUnlock();
        }
    }

    @Override
    public String toString() {
        fullyLock();
        try {
            Node<E> p = head.next;
            if (p == null) {
                return "[]";
            }

            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (; ; ) {
                E e = p.item;
                sb.append(e == this ? "(this Collection)" : e);
                p = p.next;
                if (p == null) {
                    return sb.append(']').toString();
                }
                sb.append(',').append(' ');
            }
        } finally {
            fullyUnlock();
        }
    }

    @Override
    public void clear() {
        fullyLock();
        try {
            for (Node<E> p, h = head; (p = h.next) != null; h = p) {
                h.next = h;
                p.item = null;
            }
            head = last;
            // assert head.item == null && head.next == null;
            if (count.getAndSet(0) == capacity) {
                notFull.signal();
            }
        } finally {
            fullyUnlock();
        }
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return drainTo(c, Integer.MAX_VALUE);
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        if (maxElements <= 0) {
            return 0;
        }
        boolean signalNotFull = false;
        final MyReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            int n = Math.min(maxElements, count.get());
            // count.get provides visibility to first n Nodes
            Node<E> h = head;
            int i = 0;
            try {
                while (i < n) {
                    Node<E> p = h.next;
                    c.add(p.item);
                    p.item = null;
                    h.next = h;
                    h = p;
                    ++i;
                }
                return n;
            } finally {
                // Restore invariants even if c.add() threw
                if (i > 0) {
                    // assert h.item == null;
                    head = h;
                    signalNotFull = (count.getAndAdd(-i) == capacity);
                }
            }
        } finally {
            takeLock.unlock();
            if (signalNotFull) {
                signalNotFull();
            }
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     1. LinkedBlockingQueue的迭代器的实现内部类
     2. 弱一致性的迭代器实现
     3. 由于是迭代操作，需要遍历队列，因此需要同时获取两把锁
     */
    private class Itr implements Iterator<E> {
        /*
         * Basic weakly-consistent iterator.  At all times hold the next
         * item to hand out so that if hasNext() reports true, we will
         * still have it to return even if lost race with a take etc.
         */

        private Node<E> current;
        private Node<E> lastRet;
        private E currentElement;

        Itr() {
            fullyLock();
            try {
                current = head.next;
                if (current != null) {
                    currentElement = current.item;
                }
            } finally {
                fullyUnlock();
            }
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        /**
         * Returns the next live successor of p, or null if no such.
         * <p>
         * Unlike other traversal methods, iterators need to handle both:
         * - dequeued nodes (p.next == p)
         * - (possibly multiple) interior removed nodes (p.item == null)
         */
        private Node<E> nextNode(Node<E> p) {
            for (; ; ) {
                Node<E> s = p.next;
                if (s == p) {
                    return head.next;
                }
                if (s == null || s.item != null) {
                    return s;
                }
                p = s;
            }
        }

        @Override
        public E next() {
            fullyLock();
            try {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                E x = currentElement;
                lastRet = current;
                current = nextNode(current);
                currentElement = (current == null) ? null : current.item;
                return x;
            } finally {
                fullyUnlock();
            }
        }

        @Override
        public void remove() {
            if (lastRet == null) {
                throw new IllegalStateException();
            }
            fullyLock();
            try {
                Node<E> node = lastRet;
                lastRet = null;
                for (Node<E> trail = head, p = trail.next;
                     p != null;
                     trail = p, p = p.next) {
                    if (p == node) {
                        unlink(p, trail);
                        break;
                    }
                }
            } finally {
                fullyUnlock();
            }
        }
    }

    static final class LBQSpliterator<E> implements Spliterator<E> {
        static final int MAX_BATCH = 1 << 25;  // max batch array size;
        final MyLinkedBlockingQueue<E> queue;
        Node<E> current;    // current node; null until initialized
        int batch;          // batch size for splits
        boolean exhausted;  // true when no more nodes
        long est;           // size estimate

        LBQSpliterator(MyLinkedBlockingQueue<E> queue) {
            this.queue = queue;
            this.est = queue.size();
        }

        @Override
        public long estimateSize() {
            return est;
        }

        @Override
        public Spliterator<E> trySplit() {
            Node<E> h;
            final MyLinkedBlockingQueue<E> q = this.queue;
            int b = batch;
            int n = (b <= 0) ? 1 : (b >= MAX_BATCH) ? MAX_BATCH : b + 1;
            if (!exhausted &&
                    ((h = current) != null || (h = q.head.next) != null) &&
                    h.next != null) {
                Object[] a = new Object[n];
                int i = 0;
                Node<E> p = current;
                q.fullyLock();
                try {
                    if (p != null || (p = q.head.next) != null) {
                        do {
                            if ((a[i] = p.item) != null) {
                                ++i;
                            }
                        } while ((p = p.next) != null && i < n);
                    }
                } finally {
                    q.fullyUnlock();
                }
                if ((current = p) == null) {
                    est = 0L;
                    exhausted = true;
                } else if ((est -= i) < 0L) {
                    est = 0L;
                }
                if (i > 0) {
                    batch = i;
                    return Spliterators.spliterator
                            (a, 0, i, Spliterator.ORDERED | Spliterator.NONNULL |
                                    Spliterator.CONCURRENT);
                }
            }
            return null;
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            final MyLinkedBlockingQueue<E> q = this.queue;
            if (!exhausted) {
                exhausted = true;
                Node<E> p = current;
                do {
                    E e = null;
                    q.fullyLock();
                    try {
                        if (p == null) {
                            p = q.head.next;
                        }
                        while (p != null) {
                            e = p.item;
                            p = p.next;
                            if (e != null) {
                                break;
                            }
                        }
                    } finally {
                        q.fullyUnlock();
                    }
                    if (e != null) {
                        action.accept(e);
                    }
                } while (p != null);
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super E> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            final MyLinkedBlockingQueue<E> q = this.queue;
            if (!exhausted) {
                E e = null;
                q.fullyLock();
                try {
                    if (current == null) {
                        current = q.head.next;
                    }
                    while (current != null) {
                        e = current.item;
                        current = current.next;
                        if (e != null) {
                            break;
                        }
                    }
                } finally {
                    q.fullyUnlock();
                }
                if (current == null) {
                    exhausted = true;
                }
                if (e != null) {
                    action.accept(e);
                    return true;
                }
            }
            return false;
        }

        @Override
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.NONNULL |
                    Spliterator.CONCURRENT;
        }

    }

    @Override
    public Spliterator<E> spliterator() {
        return new LBQSpliterator<E>(this);
    }

    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {

        fullyLock();
        try {
            // Write out any hidden stuff, plus capacity
            s.defaultWriteObject();

            // Write out all elements in the proper order.
            for (Node<E> p = head.next; p != null; p = p.next) {
                s.writeObject(p.item);
            }

            // Use trailing null as sentinel
            s.writeObject(null);
        } finally {
            fullyUnlock();
        }
    }

    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        // Read in capacity, and any hidden stuff
        s.defaultReadObject();

        count.set(0);
        last = head = new Node<E>(null);

        // Read in all elements and place in queue
        for (; ; ) {
            @SuppressWarnings("unchecked")
            E item = (E) s.readObject();
            if (item == null) {
                break;
            }
            add(item);
        }
    }
}
