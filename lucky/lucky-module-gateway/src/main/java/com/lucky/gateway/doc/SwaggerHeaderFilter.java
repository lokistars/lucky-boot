//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.lucky.gateway.doc;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SwaggerHeaderFilter
        extends AbstractGatewayFilterFactory<Object>
        implements GlobalFilter, Ordered {
    private static final String HEADER_NAME = "X-Forwarded-Prefix";
    private static final String URI = "/v3/api-docs";

    private static final String IGNORE_URI = URI + "/default";


    /**
     * 如果配置gateway.discovery.locator.enabled:true 过滤器失效,需要在filters中手动指定
     *
     * @param config 配置类
     * @return
     */
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            if (!StringUtils.endsWithIgnoreCase(path, URI)) {
                return chain.filter(exchange);
            } else {
                String basePath = path.substring(0, path.lastIndexOf(URI));
                ServerHttpRequest newRequest = request.mutate().header(HEADER_NAME, new String[]{basePath}).build();
                ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
                return chain.filter(newExchange);
            }
        };
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            if (StringUtils.endsWithIgnoreCase(path, IGNORE_URI)) {
                //Route oldRoute = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                //Route route = Route.async().predicate((key) -> true).id(Objects.requireNonNull(oldRoute).getId()).uri(path).build();
                path = path.replace("/default", "");
                ServerHttpRequest build = request.mutate().path(path).build();
                exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, build);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            //请求完成回调方法 可以在此完成计算请求耗时等操作
        }));
    }

    @Override
    public int getOrder() {
        return 49;
    }
}
