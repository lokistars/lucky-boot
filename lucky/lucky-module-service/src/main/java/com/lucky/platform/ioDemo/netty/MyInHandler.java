package com.lucky.platform.ioDemo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author: Loki
 * @data: 2021-09-25 13:17
 **/
public class MyInHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client  registed...");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client active...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
//        CharSequence str = buf.readCharSequence(buf.readableBytes(), CharsetUtil.UTF_8);
        CharSequence str = buf.getCharSequence(0,buf.readableBytes(), CharsetUtil.UTF_8);
        System.out.println(str);
        ctx.writeAndFlush(buf);
    }

}
