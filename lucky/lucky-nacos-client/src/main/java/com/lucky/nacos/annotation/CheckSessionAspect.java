package com.lucky.nacos.annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author Nuany
 */
@Aspect
@Lazy(false)
@Component
public class CheckSessionAspect {
    private static final Logger log = LoggerFactory.getLogger(CheckSessionAspect.class);

    /**
     * 定义切入点
     */
    @Pointcut("@annotation(com.lucky.nacos.annotation.CheckSession)")
    private void cut() {
        // do nothing
    }

    /**
     * 前置通知
     * @param joinPoint
     */
    @Before("cut()")
    public void Before(JoinPoint joinPoint){
        System.out.println("******拦截前的逻辑******");
        System.out.println("目标方法名为:" + joinPoint.getSignature().getName());
        System.out.println("目标方法所属类的简单类名:" + joinPoint.getSignature().getDeclaringType().getSimpleName());
        System.out.println("目标方法所属类的类名:" + joinPoint.getSignature().getDeclaringTypeName());
        System.out.println("目标方法声明类型:" + Modifier.toString(joinPoint.getSignature().getModifiers()));
        System.out.println("被代理的对象:" + joinPoint.getTarget());
        System.out.println("代理对象自己:" + joinPoint.getThis());
    }


    /**
     * 环绕通知
     * 环绕通知非常强大，可以决定目标方法是否执行，什么时候执行，执行时是否需要替换方法参数，执行完毕是否需要替换返回值。
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("cut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        CheckSession session = this.getMethodAnnotation(joinPoint);
        //获取到请求的属性
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert sra != null;
        //获取到请求对象
        HttpServletRequest request = sra.getRequest();
        String url = request.getRequestURL().toString();
        //获取请求的方法，是Get还是Post请求
        String method = request.getMethod();
        String queryString = request.getQueryString();
        log.info("{url:{}, method:{}, queryString:{}}", url, method, queryString);
        String header = request.getHeader(session.value());
        if(header!=null){
            if (header.equals("demoData321")){
                return joinPoint.proceed();
            }
        }
        // 获取方法传入参数
        Object[] params = joinPoint.getArgs();
        // 获取方法执行结果
        //joinPoint.proceed();
        return "验证失败";
    }

    /**
     * 获取方法中声明的注解
     *
     * @param joinPoint
     * @return
     * @throws NoSuchMethodException
     */
    private CheckSession getMethodAnnotation(JoinPoint joinPoint) throws NoSuchMethodException {
        // 获取方法名
        String methodName = joinPoint.getSignature().getName();
        // 反射获取目标类
        Class<?> targetClass = joinPoint.getTarget().getClass();
        // 拿到方法对应的参数类型
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        // 根据类、方法、参数类型（重载）获取到方法的具体信息
        Method objMethod = targetClass.getMethod(methodName, parameterTypes);
        // 拿到方法定义的注解信息
        return objMethod.getDeclaredAnnotation(CheckSession.class);
    }

    /**
     * 获取类中声明的注解
     *
     * @param joinPoint
     * @return
     * @throws NoSuchMethodException
     */
    private CheckSession getClassAnnotation(JoinPoint joinPoint) throws NoSuchMethodException {
        // 反射获取目标类
        Class<?> targetClass = joinPoint.getTarget().getClass();
        return targetClass.getDeclaredAnnotation(CheckSession.class);
    }
}
