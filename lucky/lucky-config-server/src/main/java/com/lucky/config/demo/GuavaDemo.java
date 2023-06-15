package com.lucky.config.demo;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @program: lucky
 * @description:
 * @author: Loki
 * @data: 2023-06-15 21:29
 **/
public class GuavaDemo {

    public static void main(String[] args) {
        FuturesTest();
    }


    private static void FuturesTest(){
        ExecutorService threadPool = Executors.newCachedThreadPool();
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        Futures.addCallback(Futures.allAsList(list.stream().map(it->{
            SettableFuture<Integer> future = SettableFuture.create();
            System.out.println(it);
            future.set(it);
            return future;
        }).collect(Collectors.toSet())),new FutureCallback<>(){
            @Override
            public void onSuccess(List<Integer> result) {
                System.out.println("回调成功"+ result);
            }
            @Override
            public void onFailure(Throwable t) {
                System.out.println("回调失败");
            }
        },threadPool);
    }
}
