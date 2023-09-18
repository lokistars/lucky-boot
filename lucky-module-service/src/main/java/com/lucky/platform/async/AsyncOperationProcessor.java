package com.lucky.platform.async;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: lucky-module-service
 * @description:
 * @author: Loki
 * @data: 2023-02-22 14:50
 **/
public final class AsyncOperationProcessor {


    private AsyncOperationProcessor() {
    }

    private static final AsyncOperationProcessor instance = new AsyncOperationProcessor();

    public static AsyncOperationProcessor getInstance() {
        return instance;
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor((runnable)->{
        Thread thread = new Thread(runnable);
        thread.setName("AsyncOperationProcessor");
        return thread;
    });

    public void process(IAsyncOperation operation){
        if (Objects.isNull(operation)){
            return;
        }
        executor.submit(()->{
            operation.doAsync();
            
            operation.doFinish();
        });
    }


}
