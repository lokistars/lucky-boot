package com.lucky.config.controller;

import com.lucky.config.entity.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @program: lucky
 * @description: lucky
 * @author: Loki
 * @data: 2023-06-15 20:50
 **/
@RestController
@RequestMapping("/lucky")
public class LuckyController {

    @GetMapping("/1")
    public Mono<User> getTest1(){
        User user = new User();
        user.setName("getTest1");
        return Mono.just(user);
    }

    @GetMapping("/2")
    public Mono<String> getTest2(){
        return Mono.just("getTest2");
    }


}
