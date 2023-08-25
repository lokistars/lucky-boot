package com.lucky.platform.service.Impl;

import com.lucky.platform.entity.User;
import com.lucky.platform.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author: Loki
 * @data: 2021-10-15 21:10
 **/
@Service
public class MysqlLock{

    private static final Logger log = LoggerFactory.getLogger(MysqlLock.class);

    @Autowired
    private UserMapper userMapper;

    private ThreadLocal<User> userThreadLocal;

    public void setUserThreadLocal(ThreadLocal<User> userThreadLocal) {
        this.userThreadLocal = userThreadLocal;
    }

    public void lock() {
        if (tryLock()){
            log.info("尝试加锁");
            return;
        }
        // 2.休眠
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 3.递归再次调用
        lock();
    }

    public boolean tryLock() {
        try {
            final User user = userThreadLocal.get();
            userMapper.insert(user);
            log.info("加锁对象："+user.getVersion());
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public void unlock() {
        final User user = userThreadLocal.get();
        userMapper.deleteById(user.getId());
        log.info("解锁对象："+user.getVersion());
        userThreadLocal.remove();
    }
}
