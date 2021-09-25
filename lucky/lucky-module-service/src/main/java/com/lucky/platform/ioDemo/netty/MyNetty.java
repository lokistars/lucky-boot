package com.lucky.platform.ioDemo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author: Loki
 * @data: 2021-09-24 17:56
 **/
public class MyNetty {
    public static void main(String[] args) throws Exception{

        //clientMode();
        serverMode();

    }

    /**
     * netty 客户端模型
     */
    private static void clientMode() throws Exception{
        //Netty客户端只有一个NioEventLoopGroup，就是用来处理与服务端通信的线程组
        //NioEventLoopGroup可以理解为一个线程池，内部维护了一组线程，
        //每个线程负责处理多个Channel上的事件，而一个Channel只对应于一个线程，这样可以回避多线程下的数据同步问题
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        // 客户端模式
        NioSocketChannel client = new NioSocketChannel();
        // 注册到event loop
        group.register(client);
        //处理或截获通道的接收和发送数据
        //每个新的通道Channel，都会创建一个新的ChannelPipeline，并将器pipeline附加到channel中
        ChannelPipeline pipeline = client.pipeline();
        pipeline.addLast(new MyInHandler());
        //reactor 异步特征  连接客户端
        ChannelFuture connect = client.connect(new InetSocketAddress("10.10.11.115", 9090));
        // 等待连接成功, 更改同步
        ChannelFuture sync = connect.sync();
        ByteBuf buf = Unpooled.copiedBuffer("hello world".getBytes());
        // 发送消息
        ChannelFuture send = client.writeAndFlush(buf);
        send.sync();
        //
        sync.channel().closeFuture().sync();
        System.out.println("client over....");
    }


    /**
     * 服务端
     */
    private static void serverMode(){
        // 线程组 一个Netty服务端启动时，通常会有两个NioEventLoopGroup：
        // 一个是监听线程组，主要是监听客户端请求，另一个是工作线程组，主要是处理与客户端的数据通讯
        //NioEventLoopGroup可以理解为一个线程池，内部维护了一组线程，
        //每个线程负责处理多个Channel上的事件，而一个Channel只对应于一个线程，这样可以回避多线程下的数据同步问题
        NioEventLoopGroup group = new NioEventLoopGroup(1);

    }
}
