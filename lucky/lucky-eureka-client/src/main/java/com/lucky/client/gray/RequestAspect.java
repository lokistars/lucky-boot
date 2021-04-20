package com.lucky.client.gray;

import io.jmnarloch.spring.cloud.ribbon.support.RibbonFilterContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: Loki
 * @data: 2021-04-19 19:59
 * 拦截器
 **/
@Aspect
@Component
public class RequestAspect {

    /*@Pointcut("@annotation(com.lucky.client.controller.*)")
    public void anyMethod(){

    }

    @Before(value = "anyMethod()")
    public void Before(JoinPoint point){

        //RibbonFilterContextHolder.getCurrentContext().add("version","v1");
    }*/
}
