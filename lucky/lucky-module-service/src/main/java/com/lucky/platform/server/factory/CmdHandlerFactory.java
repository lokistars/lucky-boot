package com.lucky.platform.server.factory;

import com.google.protobuf.GeneratedMessageV3;
import com.lucky.platform.server.handler.GameMsgHandlerContext;
import com.lucky.platform.server.handler.ICmdHandler;
import com.lucky.platform.utils.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: lucky-module-service
 * @description: 消息处理器
 * @author: Loki
 * @data: 2023-03-14 21:36
 **/
public class CmdHandlerFactory {

    static private final Logger LOG = LoggerFactory.getLogger(CmdHandlerFactory.class);

    private CmdHandlerFactory() {
    }

    static private final Map<Class<?>, ICmdHandler<?extends GeneratedMessageV3>> handlerMap = new ConcurrentHashMap<>(16);

    public static void main(String[] args) {
        init();
    }

    static public void init(){
        Class<ICmdHandler> handlerClass = ICmdHandler.class;

        PackageUtil.listSubClazz(handlerClass.getPackage().getName(),true,handlerClass).forEach(clazz -> {
            if (null == clazz || (clazz.getModifiers() & Modifier.ABSTRACT) != 0) {
                return;
            }

            // 获取所有方法
            Method[] methods = clazz.getDeclaredMethods();

            Class<?> cmdClass = null;

            for (Method method : methods) {

                // 过滤非handle方法
                if (!method.getName().equals("handle")){
                    continue;
                }

                // 获取参数类型
                Class<?>[] types = method.getParameterTypes();

                // 过滤参数不为2个的方法
                if (types.length < 2 || types[1] == GeneratedMessageV3.class
                        || !GeneratedMessageV3.class.isAssignableFrom(types[1])){
                    continue;
                }

                cmdClass = types[1];
                break;
            }

            if (cmdClass == null){
                return;
            }

            try {
                // 通过反射创建实例,无参构造函数
                ICmdHandler<?> handler = (ICmdHandler<?>) clazz.getDeclaredConstructor().newInstance();
                handlerMap.put(cmdClass,handler);


            }catch (Exception ex){
                LOG.error(ex.getMessage(),ex);
            }

        });
    }

    static public ICmdHandler<? extends GeneratedMessageV3> create(Class<?> cmdClass){
        if (null == cmdClass){
            return null;
        }

        return handlerMap.get(cmdClass);
    }

}
