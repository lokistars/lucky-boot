package com.lucky.platform.server.handler;

import com.google.protobuf.GeneratedMessageV3;
import com.lucky.platform.server.protocolBuf.GameMsgProtocol;
import com.lucky.platform.utils.ChannelGroupUtils;
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

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            ChannelGroupUtils.addChannels(ctx.channel());
            super.channelActive(ctx);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof GameMsgProtocol.UserLoginCmd) {
            GameMsgProtocol.UserLoginCmd loginCmd = (GameMsgProtocol.UserLoginCmd) msg;
            new UserLonginCmdHandler().handle(ctx, loginCmd);
        }else if(msg instanceof GameMsgProtocol.UserEntryCmd){
            GameMsgProtocol.UserEntryCmd entryCmd = (GameMsgProtocol.UserEntryCmd) msg;
            new UserEntryCmdHandler().handle(ctx,entryCmd);
        }else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd){
            GameMsgProtocol.WhoElseIsHereCmd who = (GameMsgProtocol.WhoElseIsHereCmd) msg;
            new WhoElseIsHereHandler().handle(ctx,who);
        }
    }

}
