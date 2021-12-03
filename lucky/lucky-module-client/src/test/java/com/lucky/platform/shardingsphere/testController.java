package com.lucky.platform.shardingsphere;

import com.lucky.platform.ClientApplication;
import com.lucky.platform.entity.User;
import com.lucky.platform.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author: Loki
 * @data: 2021-11-21 14:12
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClientApplication.class)
public class testController {

    @Autowired
    private UserService userService;

    @Test
    public void test() {
        User user = new User();
        user.setId(1);
        userService.addUser();
        final List<User> users = userService.getUser(user);
        users.forEach(System.out::println);
        System.out.println("323");
    }

}
