package com.lucky.platform.controller;


import com.alibaba.fastjson.JSONObject;
import com.lucky.platform.entity.User;
import com.lucky.platform.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 用户登录 前端控制器
 * </p>
 *
 * @author Nuany
 * @since 2020-09-12
 */
@Api(value = "UserController",tags = "用户登录")
@RestController
@RequestMapping("/platform/User")
public class UserController {
    @Autowired
    private UserService userService;


    @ApiOperation(value = "login")
    @GetMapping("/login")
    public String userLogin(String username, String password) {
        System.out.println(username + "密码" + password);
        //List<User> login = userService.login(username, password);
        return "登录失败";
    }


    @GetMapping("/doLock")
    public void doLock(User user) {
        userService.doLock(user);
    }
}

