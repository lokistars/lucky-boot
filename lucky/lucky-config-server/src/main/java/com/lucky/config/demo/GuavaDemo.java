package com.lucky.config.demo;

import com.google.common.util.concurrent.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

/**
 * @program: lucky
 * @description:
 * @author: Loki
 * @data: 2023-06-15 21:29
 **/
public class GuavaDemo {

    private static final ExecutorService threadPool = Executors.newFixedThreadPool(2);

    private static final SettableFuture<Integer> future = SettableFuture.create();

    public static ListenableFuture<Integer> getFuture(){
        return future;
    }

    public static void setFuture(int i){
        future.set(i);
    }

    public static void main(String[] args) {
        futuresTest2();
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
        setFuture(3);
    }


    private static void futuresTest1(){

        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        Futures.addCallback(Futures.allAsList(list.stream().map(it->{
            System.out.println("执行业务逻辑" + it);
            setFuture(it);
            return getFuture();
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

    private static void futuresTest2(){
        getFuture().addListener(()-> System.out.println("执行监听"), MoreExecutors.directExecutor());
        Futures.addCallback(JdkFutureAdapters.listenInPoolThread(getFuture()), new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                System.out.println("收到信息"+result);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        },MoreExecutors.directExecutor());
    }

    private static void futuresTest3(){
        ListenableFuture<String> transform = Futures.transform(getFuture(), (state) -> {
            System.out.println("getFuture 结果："+state);
            return "张三";
        }, MoreExecutors.directExecutor());
        Futures.addCallback(transform, new FutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("收到信息："+result);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        },MoreExecutors.directExecutor());
    }
}
