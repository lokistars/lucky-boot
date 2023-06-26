package com.lucky.config.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Loki
 * @data: 2021-12-03 23:33
 **/
@Component
public class UserService {

    private void eat(){
        System.out.println("被执行了");
    }

    /**
     * 使用注解形式不要返回Mono<ServerResponse>
     * @param request
     * @return
     */
    public Mono<ServerResponse> getTest(ServerRequest request){
        List<String> list = new ArrayList<>();
        list.add("getTest3");
        list.add("getTest3");
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).
                body(Mono.just(list),ArrayList.class);
    }
}
