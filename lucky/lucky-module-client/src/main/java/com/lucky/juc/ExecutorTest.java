package com.lucky.juc;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: Loki
 * @data: 2021-12-14 13:31
 **/
public class ExecutorTest {

    public static void main(String[] args) throws Exception{
        final ArrayList<Callable<Integer>> list = Lists.newArrayList();
        list.add(()->{
            System.out.println("123");
            return 1;
        });
        final ExecutorService service = new ThreadPoolExecutor(1,2,60L,
                TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>(5),Executors.defaultThreadFactory());
        service.submit(()->{
            System.out.println("32132132");
        });
        service.invokeAny(list);
        service.shutdown();

    }
}
