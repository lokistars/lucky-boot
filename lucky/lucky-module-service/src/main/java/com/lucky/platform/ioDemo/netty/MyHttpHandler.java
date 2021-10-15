package com.lucky.platform.ioDemo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * @author: Loki
 * @data: 2021-10-06 15:21
 **/
public class MyHttpHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger log = LoggerFactory.getLogger(MyHttpHandler.class);
    /**
     * 定义一个channel组 管理所有的channel
     */
    private static final ChannelGroup CHANNELS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            CHANNELS.add(ctx.channel());
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 把值保存起来
        ctx.channel().attr(AttributeKey.valueOf("userId")).set("1");
        if (msg instanceof HttpRequest){
            // 获取浏览器传入的地址
            System.out.println("获取请求地址："+ctx.channel().remoteAddress());
            final HttpRequest request = (HttpRequest) msg;
            //获取请求地址，进行过滤
            URI uri = new URI(request.uri());
            if ("/favicon.ico".equals(uri.getPath())){
                System.out.println("favicon.ico");
                return;
            }
            final ByteBuf buf = Unpooled.copiedBuffer("Hello", CharsetUtil.UTF_8);
            //构造一个httpResponse , 设置响应消息, 回复给浏览器
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
            System.out.println("msg:"+msg);
        }
    }
}
