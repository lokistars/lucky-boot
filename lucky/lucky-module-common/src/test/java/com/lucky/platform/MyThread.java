package com.lucky.platform;

import java.util.concurrent.TimeUnit;

/**
 * @program: lucky
 * @description: 多线程
 * @author: Loki
 * @create: 2020-11-05 15:56
 **/
public class MyThread {
    public static void main(String[] args) {
        MyThread01 myThread01 = new MyThread01();
        myThread01.start();
        System.out.println(myThread01.getState());
        new Thread(new MyRunnable()).start();

    }
}

/**
 * 实现 Thread 类
 */
class MyThread01 extends Thread{
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                TimeUnit.MICROSECONDS.sleep(10000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("1");
        }
    }
}

/**
 * 继承 Runnable 接口
 */
class MyRunnable implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                TimeUnit.MICROSECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("2");
        }
    }
}
