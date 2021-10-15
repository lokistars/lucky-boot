package com.lucky.platform.ioDemo.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * reactor 模型
 * @author: Loki
 * @data: 2021-09-24 17:56
 **/
public class MyNetty {
    private static final Logger log = LoggerFactory.getLogger(MyNetty.class);

    public static void main(String[] args) throws Exception{

        //clientNetty();
        serverMode();
    }

    /**
     * netty 客户端模型
     */
    private static void clientMode() throws Exception{
        //Netty客户端只有一个NioEventLoopGroup，就是用来处理与服务端通信的线程组
        //NioEventLoopGroup可以理解为一个线程池，内部维护了一组线程，
        //每个线程负责处理多个Channel上的事件，而一个Channel只对应于一个线程，这样可以回避多线程下的数据同步问题
        EventLoopGroup group = new NioEventLoopGroup(1);
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

    private static void clientNetty() throws Exception{

        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            final ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new MyInHandler());
                        }
                    });
            final ChannelFuture sync = bootstrap.connect("10.10.11.115", 9090).sync();

            //对关闭通道进行监听,关闭事件触发后
            sync.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }

    /**
     * 服务端
     */
    private static void serverMode() throws Exception{
        // 线程组 一个Netty服务端启动时，通常会有两个NioEventLoopGroup：
        // 一个是监听线程组，主要是监听客户端请求，另一个是工作线程组，主要是处理与客户端的数据通讯
        //NioEventLoopGroup可以理解为一个线程池，内部维护了一组线程，都是无限循环
        //每个线程负责处理多个Channel上的事件，而一个Channel只对应于一个线程，这样可以回避多线程下的数据同步问题
        //默认是NettyRuntime.availableProcessors() * 2 当前CUP核数 * 2
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {
            //创建服务器端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)// 服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG,2)//设置线程队列得到的连接数
                    .childOption(ChannelOption.SO_KEEPALIVE,true) // 设置保持活跃连接状态
                    //.handler(new LoggingHandler(LogLevel.INFO)) //对应的 bossGroup
                    .childHandler(new MyChannelInitializer()); //设置Handler 对应的workerGroup
            //启动服务器并且绑定端口,且是同步的
            final ChannelFuture sync = bootstrap.bind(9014).sync();
            //监听端口是否绑定成功
            sync.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (sync.isSuccess()){
                        System.out.println("监听端口9090绑定成功");
                    }
                }
            });
            //对关闭通道进行监听,关闭事件触发后
            sync.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
