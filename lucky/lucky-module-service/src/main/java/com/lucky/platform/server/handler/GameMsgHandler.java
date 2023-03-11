package com.lucky.platform.server.handler;

import com.lucky.platform.config.RedisConfig;
import com.lucky.platform.server.protocolBuf.GameMsgProtocol;
import com.lucky.platform.utils.ChannelGroupUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: lucky-module-service
 * @description: 消息处理器
 * @author: Loki
 * @data: 2023-02-21 21:36
 **/
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOG = LoggerFactory.getLogger(GameMsgHandler.class);

    private GameMsgHandlerContext context = null;

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
        ChannelId id = ctx.channel().id();

        context = new GameMsgHandlerContext(ctx,id);

        if (msg instanceof GameMsgProtocol.UserLoginCmd) {
            GameMsgProtocol.UserLoginCmd loginCmd = (GameMsgProtocol.UserLoginCmd) msg;
            new UserLoginCmdHandler().handle(context, loginCmd);
        }else if(msg instanceof GameMsgProtocol.UserEntryCmd){
            GameMsgProtocol.UserEntryCmd entryCmd = (GameMsgProtocol.UserEntryCmd) msg;
            new UserEntryCmdHandler().handle(context,entryCmd);
        }else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd){
            GameMsgProtocol.WhoElseIsHereCmd who = (GameMsgProtocol.WhoElseIsHereCmd) msg;
            new WhoElseIsHereHandler().handle(context,who);
        }else if (msg instanceof GameMsgProtocol.UserMoveToCmd){
            GameMsgProtocol.UserMoveToCmd who = (GameMsgProtocol.UserMoveToCmd) msg;
            new UserMoveCmdHandler().handle(context,who);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx){

        try {
            super.handlerRemoved(ctx);
            if (Objects.nonNull(context)){
                context.remove(ctx.channel().id());
                ChannelGroupUtils.removeChannels(ctx.channel());
                ChannelGroupUtils.removeByUser(context.getUserId());


                GameMsgProtocol.UserQuitResult.Builder builder = GameMsgProtocol.UserQuitResult.newBuilder();
                builder.setQuitUserId(context.getUserId());
                GameMsgProtocol.UserQuitResult build = builder.build();
                ChannelGroupUtils.send(build);
            }
        }catch (Exception ex){
            LOG.error(ex.getMessage(),ex);
        }

    }
}
