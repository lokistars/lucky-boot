package com.lucky.platform.server;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.lucky.platform.server.handler.GameMsgRecognizer;
import com.lucky.platform.server.protocolBuf.GameMsgProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * @author: Loki
 * @data: 2021-10-06 15:21
 **/
public class MessageDecoder extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(MessageDecoder.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == ctx || null == msg ){
            return;
        }
        if (msg instanceof HttpRequest){
            // 获取浏览器传入的地址
            System.out.println("获取请求地址："+ctx.channel().remoteAddress());
            final HttpRequest request = (HttpRequest) msg;
            //获取请求地址，进行过滤
            URI uri = new URI(request.uri());
            if ("/favicon.ico".equals(uri.getPath())){
                LOG.info("favicon.ico");
                return;
            }
            final ByteBuf buf = Unpooled.copiedBuffer("Hello", CharsetUtil.UTF_8);
            //构造一个ht tpResponse , 设置响应消息, 回复给浏览器
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,buf);
            //设置响应类型 content-type
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            //设置响应文本长度
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,buf.readableBytes());
            //将设置好的Response对象返回
            ctx.writeAndFlush(response);
        }else if (msg instanceof BinaryWebSocketFrame){
            final BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msg;
            final ByteBuf byteBuf = inputFrame.content();
            // 读取长度
            short len = byteBuf.readShort();
            // 读取消息号
            short msgCode = byteBuf.readShort();
            // 拿到消息体
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);

            // 业务逻辑
            GeneratedMessageV3 cmd = null;

            Message.Builder byMsgCode = GameMsgRecognizer.getMsgBuilderByMsgCode(msgCode);

            switch (msgCode){
                case GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE:
                    cmd = GameMsgProtocol.UserEntryCmd.parseFrom(bytes);
                    break;
                case GameMsgProtocol.MsgCode.USER_LOGIN_CMD_VALUE:
                    cmd = GameMsgProtocol.UserLoginCmd.parseFrom(bytes);
                    break;
                case GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE:
                    cmd = GameMsgProtocol.WhoElseIsHereCmd.parseFrom(bytes);
                    break;
                default:
                    break;
            }

            if (null != cmd){
                ctx.fireChannelRead(cmd);
            }
        }

    }
}
