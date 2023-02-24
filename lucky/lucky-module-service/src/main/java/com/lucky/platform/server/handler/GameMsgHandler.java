package com.lucky.platform.server.handler;

import com.google.protobuf.GeneratedMessageV3;
import com.lucky.platform.server.protocolBuf.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: lucky-module-service
 * @description: 消息处理器
 * @author: Loki
 * @data: 2023-02-21 21:36
 **/
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOG = LoggerFactory.getLogger(GameMsgHandler.class);

    /**
     * 定义一个channel组 管理所有的channel,必须是static
     */
    private static final ChannelGroup CHANNELS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            CHANNELS.add(ctx.channel());
            super.channelActive(ctx);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        if (msg instanceof GameMsgProtocol.UserLoginCmd) {
            GameMsgProtocol.UserLoginCmd entryCmd = (GameMsgProtocol.UserLoginCmd) msg;
            new UserLonginCmdHandler().handle(channelHandlerContext, entryCmd);
        }
    }

}
