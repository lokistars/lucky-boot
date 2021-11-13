package com.lucky.design;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 可以使用jdk自带的 Observer java.util
 * @program: lucky
 * @description: 观察者模式, 主要角色  抽象观察者  抽象被观察者, 真实观察者，真实被观察者
 * @author: loki
 * @create: 2020-11-05 09:52
 **/
public class Observer{
    public static void main(String[] args) {
        UpEvent up = new UpEvent();
        // 订阅消息
        CatObserver cat = new CatObserver(up);
        //发布消息
        up.onOpen();
    }

}

/**
 * 抽象事件
 */
interface Event{
    /**
     *  注册观察者
     * @param ob
     */
    void registerObserver(Observers ob);

    /**
     * 删除观察者
     * @param ob
     */
    void removeObserver(Observers ob);
}

/**
 * 被观察者
 */
class UpEvent implements  Event{
    /**
     * 存放观察者对象
     */
    List<Observers> observer = new CopyOnWriteArrayList<>();

    @Override
    public void registerObserver(Observers ob) {
        observer.add(ob);
    }

    @Override
    public void removeObserver(Observers ob) {
        if(observer.indexOf(ob)>0){
            observer.remove(ob);
        }
    }
    public  void onOpen(){
        String msg = "消息体";
        for (Observers observers : observer) {
            System.out.println(observers);
            observers.action("msg");
        }
    }
}

/**
 * 观察者
 */
abstract class Observers{
    abstract void action(String  msg);
}

/**
 *  真实观察者
 */
class CatObserver extends Observers{

    CatObserver(Event event){
        event.registerObserver(this);
    }

    @Override
    void action(String  msg) {
        System.out.println(msg);
    }
}
