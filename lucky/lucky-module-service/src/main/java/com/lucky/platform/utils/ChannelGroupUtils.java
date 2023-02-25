package com.lucky.platform.utils;

import com.lucky.platform.entity.User;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    static private final Map<Integer, User> MAPS = new ConcurrentHashMap<>(16);

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


    static public void addUser(User u) {
        if (null == u) {
            return;
        }
        MAPS.putIfAbsent(u.getUserId(), u);
    }

    static public void removeByUser(int userId) {
        if (userId <= 0) {
            return;
        }
        MAPS.remove(userId);
    }

    static public Collection<User> listUser() {
        return MAPS.values();
    }

}
