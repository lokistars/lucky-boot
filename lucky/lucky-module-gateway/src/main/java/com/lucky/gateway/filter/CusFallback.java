package com.lucky.gateway.filter;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author: Loki
 * @data: 2021-04-24 16:15
 * 自定义zuul回退机制处理器
 **/
public class CusFallback implements FallbackProvider {
    /**
     * api 微服务中的 serviceId 名称
     * @return 表示需要针对此服务做回退处理
     */
    @Override
    public String getRoute() {
        // 如果所有服务都支持 则return "*"
        return "client";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {

        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return HttpStatus.OK.value();
            }

            @Override
            public String getStatusText() throws IOException {
                return HttpStatus.OK.getReasonPhrase();
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(){{
                        put("code","9001");
                        put("massage","系统错误,请求失败!");
                    }};
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new ByteArrayInputStream(obj.toString().getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setCacheControl(MediaType.APPLICATION_JSON_VALUE);
                return headers;
            }
        };
    }
}
