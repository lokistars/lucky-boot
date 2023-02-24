package com.lucky.platform.async;

/**
 * @program: lucky-module-service
 * @description:
 * @author: Loki
 * @data: 2023-02-22 15:16
 **/
public interface IAsyncOperation {

    /**
     * 执行异步操作
     */
    void doAsync();

    /**
     * 执行完成
     */
    void doFinish();

}
