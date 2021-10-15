package com.lucky.platform.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lucky.platform.entity.User;
import com.lucky.platform.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lucky.platform.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 用户登录 服务实现类
 * </p>
 *
 * @author Nuany
 * @since 2020-09-12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MysqlLock lock;

    private ThreadLocal<User> userLock = new ThreadLocal<>();

    /**
     * 认证业务
     *
     * @param userName 用户输入的用户名
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("userName", userName)
                .eq("user_stats", 0);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 模拟不同的用户 订单为 getId() 用户为 version
     * @param user
     * @return
     */
    @Override
    public User doLock(User user) {
        log.info("用户：{}",user.getVersion()+"执行抢单逻辑");

        boolean type = false;
        switch (user.getUserStats()){
            case 0:
                log.info("无锁");
                type = noLock(user);
                break;
            case 1:
                log.info("JVM锁");
                type = jvmLock(user);
                break;
            case 2:
                log.info("mysql锁");
                type = mysqlLock(user);
                break;
            case 3:
                log.info("redis锁");
                type = redisLock(user);
                break;
            case 4:
                log.info("redisson锁");
                type = redissonLock(user);
                break;
            default:
            log.info("未找到");
        }
        if (type){
            log.info("用户：{}",user.getVersion()+"抢单成功");
        }else{
            log.error("用户：{}",user.getVersion()+"抢单失败");
        }
        return user;
    }

    private  boolean noLock(User user){
        return  putUser(user.getId());
    }

    private  boolean jvmLock(User user){
        String id = user.getId()+"";
        synchronized (id.intern()){
            return  putUser(user.getId());
        }
    }

    /**
     * 数据库加锁,通过ID唯一性(不能自增),插入成功的表示加锁成功
     * 其他线程循环等待加锁,插入数据
     * 业务处理完毕后删除插入的数据,让其他线程进行加锁
     * @param user
     * @return
     */
    private  boolean mysqlLock(User user){
        final Integer userId = user.getId();
        user.setId(2);
        user.setUserName("admin");
        user.setPassword("admin");
        user.setCreateTime(new Date());
        userLock.set(user);
        lock.setUserThreadLocal(userLock);
        lock.lock();
        boolean putType = false;
        try {
            putType = putUser(userId);
        }finally {
            lock.unlock();
        }
        return  putType;
    }

    private  boolean redisLock(User user){
        return  putUser(user.getId());
    }

    private  boolean redissonLock(User user){

        return  putUser(user.getId());
    }


    private boolean putUser(Integer userId){
        final User user = userMapper.selectById(userId);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AtomicInteger integer = new AtomicInteger(user.getVersion());
        if (user.getUserStats().equals(0)){
            user.setUserStats(1);
            user.setVersion(integer.incrementAndGet());
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("id",userId);
            userMapper.update(user, wrapper);
            return true;
        }
        return  false;
    }
}
