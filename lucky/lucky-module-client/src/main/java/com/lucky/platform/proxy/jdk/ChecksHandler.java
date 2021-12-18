package com.lucky.platform.proxy.jdk;

import com.lucky.platform.entity.User;
import com.lucky.platform.service.Impl.UserServiceImpl;
import com.lucky.platform.service.UserService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author: Loki
 * @data: 2021-12-18 10:28
 **/
public class ChecksHandler implements InvocationHandler {
    /**
     * 代理类中的真实对象
     */
    private Object obj;

    public void setObj(Object obj) {
        this.obj = obj;
    }

    /**
     *  Proxy类就是用来创建一个代理对象的类
     *  loader：一个classloader对象，定义了由哪个classloader对象对生成的代理类进行加载
     *  interfaces：一个interface对象数组，表示我们将要给我们的代理对象提供一组什么样的接口，如果我们提供了这样一个接口对象数组，
     *    那么也就是声明了代理类实现了这些接口，代理类就可以调用接口中声明的所有方法。
     *  h: 一个InvocationHandler对象，表示的是当动态代理对象调用方法的时候会关联到哪一个InvocationHandler对象上，并最终由其调用。
     * @param obj
     * @return
     */
    public Object getProxy(Object obj){
        this.obj = obj;
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                this.obj.getClass().getInterfaces(),this);
    }
    /**
     *  invoke
     * @param proxy 代理类代理的真实代理对象
     * @param method 我们所要调用某个对象真实的方法的Method对象
     * @param args 指代代理对象方法传递的参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invoke = method.invoke(obj, args);
        System.out.println("测试");
        return invoke;
    }

    public static void main(String[] args) {
        ChecksHandler handler = new ChecksHandler();
        UserService proxy =(UserService) handler.getProxy(new UserServiceImpl());
        System.out.println(proxy);
    }
}
