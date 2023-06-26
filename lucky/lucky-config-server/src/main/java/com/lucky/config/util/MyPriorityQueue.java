package com.lucky.config.util;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 * 基于数组实现的二叉堆结构,实现优先级队列,默认是小根堆,升序排序
 * 注释了SharedSecrets
 * @program: lucky
 * @description: 基于1.8源码研究
 * @author: Loki
 * @data: 2023-06-22 11:34
 **/
public class MyPriorityQueue<E>
        extends AbstractQueue<E>
        implements Serializable {

    private static final long serialVersionUID = -7720805057305804111L;

    /**
     * 默认初始容量
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    /**
     * 要分配的数组的最大大小
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    /**
     * 比较器，如果优先级队列使用元素的自然排序
     */
    private final Comparator<? super E> comparator;
    /**
     * 优先级队列
     */
    transient Object[] queue;
    /**
     * 优先级队列修改次数
     */
    transient int modCount = 0;
    /**
     * 优先级队列中的元素数
     */
    private int size = 0;

    public MyPriorityQueue() {
        this(DEFAULT_INITIAL_CAPACITY, null);
    }

    public MyPriorityQueue(int initialCapacity) {
        this(initialCapacity, null);
    }

    public MyPriorityQueue(Comparator<? super E> comparator) {
        this(DEFAULT_INITIAL_CAPACITY, comparator);
    }


    /**
     * 通过初始容量和比较器初始化
     *
     * @param initialCapacity 初始容量
     * @param comparator      比较器
     */
    public MyPriorityQueue(int initialCapacity,
                           Comparator<? super E> comparator) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException();
        }
        this.queue = new Object[initialCapacity];
        this.comparator = comparator;
    }

    /**
     * 创建指定元素的优先级队列
     *
     * @param c 元素
     */
    public MyPriorityQueue(Collection<? extends E> c) {
        if (c instanceof SortedSet<?>) {
            SortedSet<? extends E> ss = (SortedSet<? extends E>) c;
            this.comparator = (Comparator<? super E>) ss.comparator();
            initElementsFromCollection(ss);
        } else if (c instanceof PriorityQueue<?>) {
            MyPriorityQueue<? extends E> pq = (MyPriorityQueue<? extends E>) c;
            this.comparator = (Comparator<? super E>) pq.comparator();
            initFromPriorityQueue(pq);
        } else {
            this.comparator = null;
            initFromCollection(c);
        }
    }

    public MyPriorityQueue(MyPriorityQueue<? extends E> c) {
        this.comparator = (Comparator<? super E>) c.comparator();
        initFromPriorityQueue(c);
    }

    public MyPriorityQueue(SortedSet<? extends E> c) {
        this.comparator = (Comparator<? super E>) c.comparator();
        initElementsFromCollection(c);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) {
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    @Override
    public Iterator<E> iterator() {
        return new MyPriorityQueue.Itr();
    }

    @Override
    public int size() {
        return size;
    }

    private void initFromPriorityQueue(MyPriorityQueue<? extends E> c) {
        if (c.getClass() == MyPriorityQueue.class) {
            this.queue = c.toArray();
            this.size = c.size();
        } else {
            initFromCollection(c);
        }
    }

    private void initElementsFromCollection(Collection<? extends E> c) {
        Object[] a = c.toArray();
        if (c.getClass() != ArrayList.class) {
            a = Arrays.copyOf(a, a.length, Object[].class);
        }
        int len = a.length;
        if (len == 1 || this.comparator != null) {
            for (int i = 0; i < len; i++)
                if (a[i] == null) {
                    throw new NullPointerException();
                }
        }
        this.queue = a;
        this.size = a.length;
    }

    private void initFromCollection(Collection<? extends E> c) {
        initElementsFromCollection(c);
        heapify();
    }

    /**
     * 扩容操作
     * @param minCapacity 容量
     */
    private void grow(int minCapacity) {
        int oldCapacity = queue.length;
        // 如果比较小的时候扩充两倍,大的时候扩充百分之五十
        int newCapacity = oldCapacity + ((oldCapacity < 64) ?
                (oldCapacity + 2) :
                (oldCapacity >> 1));
        // overflow-conscious code
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            newCapacity = hugeCapacity(minCapacity);
        }
        queue = Arrays.copyOf(queue, newCapacity);
    }

    public boolean add(E e) {
        return offer(e);
    }

    /**
     * 添加数据,
     * @param e the element to add
     * @return
     */
    @Override
    public boolean offer(E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        modCount++;
        int i = size;
        // 进行扩容
        if (i >= queue.length) {
            grow(i + 1);
        }
        size = i + 1;
        // 如果添加第一条数据
        if (i == 0) {
            queue[0] = e;
        } else {
            // 不是第一条数据,进行数据上移
            siftUp(i, e);
        }
        return true;
    }

    /**
     * 只会取堆顶的数据
     * @return 返回数据
     */
    @Override
    public E poll() {
        if (size == 0) {
            return null;
        }
        // 最后一个数据的索引
        int s = --size;
        modCount++;
        // 获取堆顶的数据返回
        E result = (E) queue[0];

        // 取出最后一个元素
        E x = (E) queue[s];

        // 将最后一个数据置为null
        queue[s] = null;
        if (s != 0){
            // 下移保证安全,从头节点向下看是否堆结构
            siftDown(0, x);
        }
        return result;
    }

    @Override
    public E peek() {
        return (size == 0) ? null : (E) queue[0];
    }


    private int indexOf(Object o) {
        if (o != null) {
            for (int i = 0; i < size; i++)
                if (o.equals(queue[i]))
                    return i;
        }
        return -1;
    }

    public boolean remove(Object o) {
        int i = indexOf(o);
        if (i == -1)
            return false;
        else {
            removeAt(i);
            return true;
        }
    }

    boolean removeEq(Object o) {
        for (int i = 0; i < size; i++) {
            if (o == queue[i]) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }

    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    public Object[] toArray() {
        return Arrays.copyOf(queue, size);
    }

    public <T> T[] toArray(T[] a) {
        final int size = this.size;
        if (a.length < size){
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(queue, size, a.getClass());
        }
        System.arraycopy(queue, 0, a, 0, size);
        if (a.length > size){
            a[size] = null;
        }
        return a;
    }


    private final class Itr implements Iterator<E> {
        /**
         * Index (into queue array) of element to be returned by
         * subsequent call to next.
         */
        private int cursor = 0;

        /**
         * Index of element returned by most recent call to next,
         * unless that element came from the forgetMeNot list.
         * Set to -1 if element is deleted by a call to remove.
         */
        private int lastRet = -1;

        /**
         * A queue of elements that were moved from the unvisited portion of
         * the heap into the visited portion as a result of "unlucky" element
         * removals during the iteration.  (Unlucky element removals are those
         * that require a siftup instead of a siftdown.)  We must visit all of
         * the elements in this list to complete the iteration.  We do this
         * after we've completed the "normal" iteration.
         *
         * We expect that most iterations, even those involving removals,
         * will not need to store elements in this field.
         */
        private ArrayDeque<E> forgetMeNot = null;

        /**
         * Element returned by the most recent call to next iff that
         * element was drawn from the forgetMeNot list.
         */
        private E lastRetElt = null;

        /**
         * The modCount value that the iterator believes that the backing
         * Queue should have.  If this expectation is violated, the iterator
         * has detected concurrent modification.
         */
        private int expectedModCount = modCount;

        public boolean hasNext() {
            return cursor < size ||
                    (forgetMeNot != null && !forgetMeNot.isEmpty());
        }

        @SuppressWarnings("unchecked")
        public E next() {
            if (expectedModCount != modCount){
                throw new ConcurrentModificationException();
            }
            if (cursor < size){
                return (E) queue[lastRet = cursor++];
            }
            if (forgetMeNot != null) {
                lastRet = -1;
                lastRetElt = forgetMeNot.poll();
                if (lastRetElt != null)
                    return lastRetElt;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            if (expectedModCount != modCount)
                throw new ConcurrentModificationException();
            if (lastRet != -1) {
                E moved = MyPriorityQueue.this.removeAt(lastRet);
                lastRet = -1;
                if (moved == null)
                    cursor--;
                else {
                    if (forgetMeNot == null)
                        forgetMeNot = new ArrayDeque<>();
                    forgetMeNot.add(moved);
                }
            } else if (lastRetElt != null) {
                MyPriorityQueue.this.removeEq(lastRetElt);
                lastRetElt = null;
            } else {
                throw new IllegalStateException();
            }
            expectedModCount = modCount;
        }
    }

    public void clear() {
        modCount++;
        for (int i = 0; i < size; i++)
            queue[i] = null;
        size = 0;
    }

    private E removeAt(int i) {
        // assert i >= 0 && i < size;
        modCount++;
        int s = --size;
        if (s == i) // removed last element
            queue[i] = null;
        else {
            E moved = (E) queue[s];
            queue[s] = null;
            siftDown(i, moved);
            if (queue[i] == moved) {
                siftUp(i, moved);
                if (queue[i] != moved)
                    return moved;
            }
        }
        return null;
    }

    /**
     * 一个数插入到堆中,大根堆获取父节点 (i-1)/2
     * 让当前节点和父节点比较,如果比父节点小就交换
     * @param k 当前数据要存放的位置
     * @param x 元素值
     */
    private void siftUp(int k, E x) {
        if (comparator != null){
            siftUpUsingComparator(k, x);
        }
        else{
            siftUpComparable(k, x);
        }
    }

    /**
     * 一个数插入到堆中,大根堆获取父节点 (i-1)/2
     * @param k 当前数据要存放的位置
     * @param x 元素值
     */
    private void siftUpComparable(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if (key.compareTo((E) e) >= 0){
                break;
            }
            queue[k] = e;
            k = parent;
        }
        queue[k] = key;
    }

    /**
     * 一个数插入到堆中,大根堆获取父节点 (i-1)/2
     * @param k 当前数据要存放的位置
     * @param x 元素值
     */
    private void siftUpUsingComparator(int k, E x) {
        while (k > 0) {
            // 获取父节点位置, 当前位置-1 除以 2
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if (comparator.compare(x, (E) e) >= 0){
                break;
            }
            queue[k] = e;
            k = parent;
        }
        queue[k] = x;
    }

    /**
     * 使用比较器向下看是否堆结构
     * @param k 需要比较的头节点
     * @param x 元素
     */
    private void siftDown(int k, E x) {
        if (comparator != null){
            siftDownUsingComparator(k, x);
        }
        else{
            siftDownComparable(k, x);
        }
    }

    /**
     * 默认是小跟堆实现
     * @param k
     * @param x
     */
    private void siftDownComparable(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>)x;
        int half = size >>> 1;        // loop while a non-leaf
        while (k < half) {
            int child = (k << 1) + 1; // assume left child is least
            Object c = queue[child];
            int right = child + 1;
            if (right < size &&
                    ((Comparable<? super E>) c).compareTo((E) queue[right]) > 0){
                c = queue[child = right];
            }
            if (key.compareTo((E) c) <= 0){
                break;
            }
            queue[k] = c;
            k = child;
        }
        queue[k] = key;
    }


    private void siftDownUsingComparator(int k, E x) {
        // 计算堆的中间位置 half
        int half = size >>> 1;
        while (k < half) {
            // 获取当前节点的左孩子
            int child = (k << 1) + 1;
            Object c = queue[child];
            // 获取当前节点的右孩子
            int right = child + 1;
            // 如果存在右孩子,并且将左子节点 c 和右子节点 right,作为比较
            // 如果是小根堆实现  (o1 < o2)返回 -1, 左节点比右节点小跳过判断,获取最小的孩子节点
            if (right < size &&
                    comparator.compare((E) c, (E) queue[right]) > 0){
                c = queue[child = right];
            }
            // x 当前节点,小于最小孩子节点进行break
            if (comparator.compare(x, (E) c) <= 0){
                break;
            }
            // 最小孩子节点跟最顶层节点交换位置,丢失最顶层节点数据
            queue[k] = c;
            k = child;
        }
        queue[k] = x;
    }

    private void heapify() {
        for (int i = (size >>> 1) - 1; i >= 0; i--){
            siftDown(i, (E) queue[i]);
        }
    }

    public Comparator<? super E> comparator() {
        return comparator;
    }

    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        // Write out element count, and any hidden stuff
        s.defaultWriteObject();

        // Write out array length, for compatibility with 1.5 version
        s.writeInt(Math.max(2, size + 1));

        // Write out all elements in the "proper order".
        for (int i = 0; i < size; i++){
            s.writeObject(queue[i]);
        }
    }

    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        // Read in size, and any hidden stuff
        s.defaultReadObject();

        // Read in (and discard) array length
        s.readInt();

       // SharedSecrets.getJavaOISAccess().checkArray(s, Object[].class, size);
        queue = new Object[size];

        // Read in all elements.
        for (int i = 0; i < size; i++){
            queue[i] = s.readObject();
        }

        // Elements are guaranteed to be in "proper order", but the
        // spec has never explained what that might be.
        heapify();
    }

    public final Spliterator<E> spliterator() {
        return new MyPriorityQueue.PriorityQueueSpliterator<E>(this, 0, -1, 0);
    }

    static final class PriorityQueueSpliterator<E> implements Spliterator<E> {
        /*
         * This is very similar to ArrayList Spliterator, except for
         * extra null checks.
         */
        private final MyPriorityQueue<E> pq;
        private int index;            // current index, modified on advance/split
        private int fence;            // -1 until first use
        private int expectedModCount; // initialized when fence set

        /** Creates new spliterator covering the given range */
        PriorityQueueSpliterator(MyPriorityQueue<E> pq, int origin, int fence,
                                 int expectedModCount) {
            this.pq = pq;
            this.index = origin;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }

        private int getFence() { // initialize fence to size on first use
            int hi;
            if ((hi = fence) < 0) {
                expectedModCount = pq.modCount;
                hi = fence = pq.size;
            }
            return hi;
        }

        public MyPriorityQueue.PriorityQueueSpliterator<E> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid) ? null :
                    new MyPriorityQueue.PriorityQueueSpliterator<E>(pq, lo, index = mid,
                            expectedModCount);
        }

        @SuppressWarnings("unchecked")
        public void forEachRemaining(Consumer<? super E> action) {
            int i, hi, mc; // hoist accesses and checks from loop
            MyPriorityQueue<E> q; Object[] a;
            if (action == null){
                throw new NullPointerException();
            }
            if ((q = pq) != null && (a = q.queue) != null) {
                if ((hi = fence) < 0) {
                    mc = q.modCount;
                    hi = q.size;
                } else{
                    mc = expectedModCount;
                }
                if ((i = index) >= 0 && (index = hi) <= a.length) {
                    for (E e;; ++i) {
                        if (i < hi) {
                            if ((e = (E) a[i]) == null) // must be CME
                                break;
                            action.accept(e);
                        }
                        else if (q.modCount != mc){
                            break;
                        }else{
                            return;
                        }
                    }
                }
            }
            throw new ConcurrentModificationException();
        }

        public boolean tryAdvance(Consumer<? super E> action) {
            if (action == null)
                throw new NullPointerException();
            int hi = getFence(), lo = index;
            if (lo >= 0 && lo < hi) {
                index = lo + 1;
                @SuppressWarnings("unchecked") E e = (E)pq.queue[lo];
                if (e == null){
                    throw new ConcurrentModificationException();
                }
                action.accept(e);
                if (pq.modCount != expectedModCount){
                    throw new ConcurrentModificationException();
                }
                return true;
            }
            return false;
        }

        public long estimateSize() {
            return (long) (getFence() - index);
        }

        public int characteristics() {
            return Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.NONNULL;
        }
    }

}
