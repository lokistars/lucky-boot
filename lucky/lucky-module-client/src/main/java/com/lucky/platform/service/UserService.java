package com.lucky.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lucky.platform.entity.User;

import java.util.List;

/**
 * <p>
 * 用户登录 服务类
 * </p>
 *
 * @author Nuany
 * @since 2020-09-12
 */

public interface UserService extends IService<User> {

    /**
     * 模拟分布式锁,抢单
     * @param user
     * @return
     */
    User doLock(User user);

    List<User> getUser(User user);

    int addUser();
}
