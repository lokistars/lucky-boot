package com.lucky.config.demo.design;

/**
 * @program: lucky
 * @description: 策略模式
 * 1、抽象策略 （Strategy）角色：抽象策略角色由抽象类或接口来承担，它给出具体策略角色需要实现的接口
 * 2、具体策略（ConcreteStrategy）角色：实现封装了具体的算法或行为
 * 3、场景（Context）角色：持有抽象策略类的引用。
 * @author: loki
 * @create: 2020-11-05 09:52
 **/
public class Strategy {

    /**
    * @Description: main 具体实现
    * @Param: [args]
    * @return: void
    * @Author: loki
    * @Date: 2020/11/5
    */
    public static void main(String[] args) {
        Context context = new Context(new PolicyDog());
        String policys = context.getPolicys();
        System.out.println(policys);
    }

}

/**
 * 定义抽象策略
 */
interface  Policy{
    /**
     *  抽象事物
     * @return
     */
    String Animal();
}

/**
 * 具体策略
 */
class PolicyDog implements Policy{

    @Override
    public String Animal() {
        return "可爱的狗狗";
    }
}

/**
 * 具体策略
 */
class PolicyCat implements Policy{

    @Override
    public String Animal() {
        return "可爱的猫咪";
    }
}

/**
 * 上下文
 */
class Context{
    private Policy policy;
    public Context(Policy po){
        this.policy = po;
    }
    public  String getPolicys(){
        return policy.Animal();
    }
    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }
}