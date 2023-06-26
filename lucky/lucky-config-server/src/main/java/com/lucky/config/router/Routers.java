package com.lucky.config.router;

import com.lucky.config.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * @program: lucky
 * @description: 路由
 * @author: Loki
 * @data: 2023-06-19 22:20
 **/
@Configuration
public class Routers {

    @Bean
    public RouterFunction<ServerResponse> getTest3(UserService handler) {
        return RouterFunctions.route()
                .GET("/lucky/3",handler::getTest)
                .build();
    }
}
