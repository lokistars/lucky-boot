package com.lucky.gateway.config;

import com.github.xiaoymin.knife4j.spring.gateway.Knife4jGatewayProperties;
import com.github.xiaoymin.knife4j.spring.gateway.discover.ServiceDiscoverHandler;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Primary
@Component
public class SwaggerServiceDiscoverHandler extends ServiceDiscoverHandler {

    public SwaggerServiceDiscoverHandler(Knife4jGatewayProperties properties){
        super(properties);
    }

    @Override
    public void discover(List<String> service) {
        super.discover(service);
        this.getGatewayResources().forEach((item)->{
            item.setName(analysis(item.getName(),true));
            item.setContextPath(analysis(item.getContextPath(),true));
            item.setUrl(analysis(item.getUrl(),false));
        });
    }

    private String  analysis(String name,Boolean type){
        if (StringUtils.hasText(name)){
            String replace = name.replace("lucky-", "");
            int i = replace.indexOf("-");
            if (type){
                return replace.substring(0,i);
            }else{
                return replace.replaceFirst("^*-[a-zA-Z]+/","/");
            }
        }
        return null;
    }
}
