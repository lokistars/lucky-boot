package com.lucky.nacos.annotation;


import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;


public class CheckSessonResolver extends AbstractNamedValueMethodArgumentResolver {
    /**
     * 解析器是否支持当前参数
     * @param parameter 需要被解析的Controller参数
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return parameter.hasParameterAnnotation(CheckSesson.class) ;
    }

    /**
     * 获取当前参数的注解信息
     * @param methodParameter 需要被解析的Controller参数
     * @return
     */
    @Override
    protected NamedValueInfo  createNamedValueInfo(MethodParameter methodParameter) {
        CheckSesson annotation = methodParameter.getParameterAnnotation(CheckSesson.class);
        return new NamedValueInfo(annotation.value(),true,null);
    }

    /**
     * 进行参数的类型转换
     * @param s
     * @param methodParameter 需要被解析的Controller参数
     * @param nativeWebRequest 当前request
     * @return 转换后的参数值
     * @throws Exception
     */
    @Override
    protected Object resolveName(String s, MethodParameter methodParameter, NativeWebRequest nativeWebRequest) throws Exception {
        String parameter = nativeWebRequest.getHeader(s);
        if (parameter==null){
            return null;
        }else {
            try {
                CheckSesson sesson = methodParameter.getParameterAnnotation(CheckSesson.class);
                System.out.println(sesson.value()+"resolveName");
                return sesson.value();
            } catch (Exception e) {
                throw new IllegalArgumentException("Date format conversion error", e);
            }
        }
    }
}
