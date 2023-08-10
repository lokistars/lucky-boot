package com.lucky.config.juc;

import com.google.common.collect.Lists;
import com.lucky.config.util.MyLinkedBlockingQueue;
import com.lucky.config.util.MyThreadPoolExecutor;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author: Loki
 * @data: 2021-12-11 10:23
 **/
public class TestExecutor {

    public static void main(String[] args)throws Exception {

        final ArrayList<Callable<Integer>> list = Lists.newArrayList();
        list.add(()->{
            System.out.println("123");
            return 1;
        });

        MyThreadPoolExecutor executor = new MyThreadPoolExecutor(
                1, 1, 100L, TimeUnit.SECONDS,
                new MyLinkedBlockingQueue<Runnable>(10));


        executor.execute(() -> {
            System.out.println("1231");
        });

        executor.invokeAny(list);
        executor.shutdown();

        CompletableFuture.runAsync(()->{

        }).thenRun(()->{

        });

    }

    public static void print(int num) {
        for (int i = 31; i >= 0; i--) {
            //System.out.print((num&(1<<i))== 0 ? "0":"1");
            System.out.print((num >> i) & 1);
        }
        System.out.println();
    }
}
