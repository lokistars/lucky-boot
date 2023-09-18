package com.lucky.platform.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * 自定义一个Handler
 * @author: Loki
 * @data: 2021-09-25 13:17
 **/
public class MyInHandler extends ChannelInboundHandlerAdapter {



    /**
     * 注销后
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client  注销了...");
        super.channelUnregistered(ctx);
    }

    /**
     * 处理程序已删除,在Unregistered之后
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("已删除");
        super.handlerRemoved(ctx);
    }

    /**
     * 当通道就绪后
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client active...");
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,客户端。",CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        //耗时很长的业务,异步执行
        //用户程序自定义的普通任务,taskQueue中
        ctx.channel().eventLoop().execute(()->{
            CharSequence str = buf.getCharSequence(0,buf.readableBytes(), CharsetUtil.UTF_8);
            System.out.println(str);
            ctx.writeAndFlush(buf);
        });
        // 用户自定义定时任务 Runnable 线程, 延时时间， 延时单位 scheduleTaskQueue中
        // ctx.channel().eventLoop().schedule(()->{},5, TimeUnit.SECONDS);
        //CharSequence str = buf.readCharSequence(buf.readableBytes(), CharsetUtil.UTF_8);
    }

    /**
     * 数据读取完毕
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,客户端",CharsetUtil.UTF_8));
    }

    /**
     * 注册完成后
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client  Registered...");
    }

    /**
     * 处理异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 检测心跳机制  new IdleStateHandler
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
        }
    }
}
