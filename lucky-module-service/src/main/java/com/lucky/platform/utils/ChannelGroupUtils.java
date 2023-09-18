package com.lucky.platform.utils;

import com.lucky.platform.config.RedisConfig;
import com.lucky.platform.entity.User;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: lucky-module-service
 * @description: 管道组
 * @author: Loki
 * @data: 2023-02-25 00:08
 **/
public final class ChannelGroupUtils {

    /**
     * 定义一个channel组 管理所有的channel,必须是static
     */
    static private final ChannelGroup CHANNELS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    static RedissonClient client =  null;

    static {
        client =  RedisConfig.getInstance();
    }

    private ChannelGroupUtils() {
    }

    static public void addChannels(Channel ch) {
        if (null == ch) {
            return;
        }
        CHANNELS.add(ch);
    }

    static public void send(Object obj) {
        if (null == obj) {
            return;
        }
        CHANNELS.writeAndFlush(obj);
    }

    static public void removeChannels(Channel ch) {
        if (null == ch) {
            return;
        }
        CHANNELS.remove(ch);
    }
    static public User addUser(Integer uid,String username,String password) {
        if (null == uid || null == password) {
            return null;
        }
        User user = new User(username,password);
        user.setUserId(uid);
        user.setHeroAvatar("Hero_Skeleton");
        return addUser(user);
    }

    static public User addUser(User u) {
        if (null == u) {
            return null;
        }
        client.getBucket("user_me:"+ u.getUserId())
                .set(u,1000, TimeUnit.SECONDS);
        return u;
    }

    static public void removeByUser(int userId) {
        if (userId <= 0) {
            return;
        }
        client.getBucket("user_me:"+ userId).delete();
    }

    static public List<User> listUser() {
        Stream<String> pattern = client.getKeys().getKeysStreamByPattern("user_me:*");
        if (Objects.nonNull(pattern)){
            List<String> key = pattern.collect(Collectors.toList());
            List<User> users = new ArrayList<>();
            key.forEach(k -> {
                User user = (User) client.getBucket(k).get();
                users.add(user);
            });
            return users;
        }
        return Collections.emptyList();
    }

}
