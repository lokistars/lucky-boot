package com.lucky.nacos.annotation;


import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;


public class CheckSessonResolver extends AbstractNamedValueMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CheckSesson.class) ;
    }
    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter methodParameter) {
        CheckSesson annotation = methodParameter.getParameterAnnotation(CheckSesson.class);
        System.out.println(annotation.value());
        return null;
    }
    @Override
    protected Object resolveName(String s, MethodParameter methodParameter, NativeWebRequest nativeWebRequest) throws Exception {
        return null;
    }
}
