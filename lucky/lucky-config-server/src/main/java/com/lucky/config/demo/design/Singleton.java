package com.lucky.config.demo.design;

/**
 * @program: lucky
 * @description: 单例模式
 * @author: Loki
 * @create: 2020-11-05 15:09
 **/
public class Singleton {
    /**
     * 构造方法私有化
     */
    private Singleton(){ }
    private volatile static Singleton SINGLETON = null ;

    public static Singleton getInstance(){
        if (SINGLETON == null){
            synchronized (Singleton.class){
                if (SINGLETON == null){
                    SINGLETON = new Singleton();
                }
            }
        }
        return SINGLETON;
    }

    public static void main(String[] args) {
        Singleton ins = Singleton.getInstance();
        Singleton in = Singleton.getInstance();
        System.out.println(ins == in);
    }
}

enum SingletonEnum{
    instance;
    public void getInstance(){};
}