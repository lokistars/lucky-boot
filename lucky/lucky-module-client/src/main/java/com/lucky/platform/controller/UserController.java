package com.lucky.platform.controller;


import com.lucky.platform.entity.User;
import com.lucky.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户登录 前端控制器
 * </p>
 *
 * @author Nuany
 * @since 2020-09-12
 */
@Tag(name = "UserController",description = "用户登录")
@RestController
@RequestMapping("/platform/User")
public class UserController {
    @Autowired
    private UserService userService;


    @Operation(method = "login")
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

