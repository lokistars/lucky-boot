package com.lucky.platform.controller;

import com.lucky.platform.ClientApplication;
import com.lucky.platform.entity.User;
import com.lucky.platform.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: Loki
 * @data: 2021-10-15 13:39
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClientApplication.class)
public class TestController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserService userService;

    @Test
    public void test1(){
        User user = new User();
        user.setId(1);
        user.setUserName("ces");

        redisTemplate.opsForValue().set("123",user.getUserName());

        userService.addUser();
    }
}
