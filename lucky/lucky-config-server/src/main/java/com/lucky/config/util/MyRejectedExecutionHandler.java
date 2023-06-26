package com.lucky.config.util;

/**
 * @program: lucky
 * @description:
 * @author: Loki
 * @data: 2023-06-22 09:33
 **/
public interface MyRejectedExecutionHandler {

    void rejectedExecution(Runnable r, MyThreadPoolExecutor executor);
}
