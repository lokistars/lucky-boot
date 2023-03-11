package com.lucky.platform.server;

import com.google.protobuf.GeneratedMessageV3;
import com.lucky.platform.server.protocolBuf.GameMsgProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: lucky-module-service
 * @description: 编码器
 * @author: Loki
 * @data: 2023-02-21 22:10
 **/
public class MessageEncoder  extends ChannelOutboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(MessageEncoder.class);


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (null == ctx || null == msg) {
            return;
        }

        try {
            if (!(msg instanceof GeneratedMessageV3)) {
                super.write(ctx, msg, promise);
                return;
            }
            short msgCode = -1;
            if (msg instanceof GameMsgProtocol.UserEntryResult) {
                msgCode = GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE;
            } else if (msg instanceof GameMsgProtocol.UserLoginResult){
                msgCode = GameMsgProtocol.MsgCode.USER_LOGIN_RESULT_VALUE;
            } else if (msg instanceof GameMsgProtocol.WhoElseIsHereResult){
                msgCode = GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE;
            }else if (msg instanceof GameMsgProtocol.UserMoveToResult){
                msgCode = GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE;
            }else if (msg instanceof GameMsgProtocol.UserQuitResult){
                msgCode = GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE;
            }  else {
                LOG.error("无法识别的消息类型，msgClazz = {}", msg.getClass().getSimpleName());
                super.write(ctx, msg, promise);
                return;
            }
            // 消息体
            byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();

            ByteBuf buffer = ctx.alloc().buffer();
            buffer.writeShort((short) msgBody.length);
            buffer.writeShort(msgCode);
            buffer.writeBytes(msgBody);
            BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(buffer);
            super.write(ctx, outputFrame, promise);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
