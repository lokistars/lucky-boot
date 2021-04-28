package com.lucky.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: Loki
 * @data: 2021-04-22 15:18
 **/
@Component
public class CusZuulFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {

        return false;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        //获取请求地址
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/test1")) {
            // 设置服务名
            context.set(FilterConstants.SERVICE_ID_KEY,"client");
            // 需要跳转的地址
            context.set(FilterConstants.REQUEST_ENTITY_KEY, "/test2");
        }
        return null;
    }
}
