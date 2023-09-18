package com.lucky.platform.server.factory;

import com.lucky.platform.entity.User;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: lucky-module-service
 * @description: HandlerContext
 * @author: Loki
 * @data: 2023-03-10 10:52
 **/
public class GameMsgHandlerContext {

    /**
     * 会话 Id 和用户 Id 关系字典
     */
    static private final Map<ChannelId, Integer> _sessionIdAndUserIdMap = new ConcurrentHashMap<>(16);


    /**
     * 真实对象
     */
    private final ChannelHandlerContext ctx;

    /**
     * 远程服务器会话 Id
     */
    private final ChannelId _remoteSessionId;

    /**
     * 用户 Id
     */
    private Integer _userId = -1;

    private static AtomicInteger atomicInteger = new AtomicInteger(1);

    public GameMsgHandlerContext(ChannelHandlerContext ctx, ChannelId sessionId) {

        this.ctx = ctx;
        this._remoteSessionId = sessionId;
        _sessionIdAndUserIdMap.putIfAbsent(sessionId, atomicInteger.incrementAndGet());
        _userId = _sessionIdAndUserIdMap.getOrDefault(sessionId, -1);
    }

    public int getUserId() {
        return _userId;
    }

    public void remove(ChannelId sessionId){
        _sessionIdAndUserIdMap.remove(sessionId);
    }

    public ChannelFuture writeAndFlush(Object msgObj) {
        if (null != msgObj) {
            return ctx.writeAndFlush(msgObj);
        } else {
            return null;
        }
    }

    public Collection<Integer> listUser() {
        return _sessionIdAndUserIdMap.values();
    }

}
