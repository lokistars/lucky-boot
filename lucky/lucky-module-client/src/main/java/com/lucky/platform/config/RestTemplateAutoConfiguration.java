package com.lucky.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Nuany
 */
@Configuration
public class RestTemplateAutoConfiguration {

    /**
     * 连接超时时间
     */
    @Value("${rest.connection.timeout:15000}")
    private Integer connectionTimeout;

    /**
     * 信息读取超时时间
     */
    @Value(value = "${rest.read.timeout:5000}")
    private Integer readTimeout;


    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(getFactory());
        //这个地方需要配置消息转换器，不然收到消息后转换会出现异常
        restTemplate.setMessageConverters(getConverts());
        return restTemplate;
    }

    /**
     * 功能描述： 初始化请求工厂
     *
     * @param
     * @author wangcanfeng
     * @time 2020/10/29
     * @since v1.0
     **/
    private SimpleClientHttpRequestFactory getFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }

    /**
     * 功能描述：  设置数据转换器，
     *
     * @param
     * @author wangcanfeng
     * @time 2020/10/29
     * @since v1.0
     **/
    private List<HttpMessageConverter<?>> getConverts() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        // String转换器
        StringHttpMessageConverter stringConvert = new StringHttpMessageConverter();
        List<MediaType> stringMediaTypes = new ArrayList<MediaType>() {{
            //配置text/plain和text/html类型的数据都转成String
            add(new MediaType("text", "plain", Charset.forName("UTF-8")));
            add(MediaType.TEXT_HTML);
        }};
        stringConvert.setSupportedMediaTypes(stringMediaTypes);
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
        messageConverters.add(mappingJackson2HttpMessageConverter);
        messageConverters.add(stringConvert);
        return messageConverters;
    }
}
