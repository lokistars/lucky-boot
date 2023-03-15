package com.lucky.platform.server.handler;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.lucky.platform.config.RedisConfig;
import com.lucky.platform.entity.InternalServerMsg;
import com.lucky.platform.server.factory.CmdHandlerFactory;
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
public class GameMsgHandler extends SimpleChannelInboundHandler<InternalServerMsg> {

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
    protected void channelRead0(ChannelHandlerContext ctx, InternalServerMsg msg) throws Exception {

        context = new GameMsgHandlerContext(ctx,msg.getUserId());

        GeneratedMessageV3 msgBody = (GeneratedMessageV3) msg.getMsgBody();

        ICmdHandler<? extends GeneratedMessageV3> handler = CmdHandlerFactory.create(msgBody.getClass());

        if (Objects.nonNull(handler)){
            handler.handle(context,cast(msgBody));
        }
    }

    private <T extends GeneratedMessageV3> T cast(Object msg) {
        if (null == msg) {
            return null;
        }else{
            return (T) msg;
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
