package com.lucky.platform;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.lucky.platform.server.MyChannelInitializer;
import com.lucky.platform.server.factory.GameMsgRecognizer;
import com.lucky.platform.server.handler.MyInHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * reactor 模型
 * @author: Loki
 * @data: 2021-09-24 17:56
 **/
public class MyNetty {
    private static final Logger log = LoggerFactory.getLogger(MyNetty.class);

    private static final CountDownLatch latch = new CountDownLatch(2);

    /**
     * 网关服务器 到 游戏服务器 的 信道
     */
    private static Channel _gameServerCh;

    public static void main(String[] args) throws Exception{
        String host = "127.0.0.1";
        int port = 8001;
        serverMode(port);
        //clientNetty(host,port);
    }

    /**
     * 客户端
     * @param host 服务器地址
     * @param port 服务器端口
     * @throws Exception
     */
    private static void clientNetty(String host, int port) throws Exception{
        latch.wait();
        String strUri = "ws://" + host + ":" + port + "/websocket";

        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            final WebSocketClientHandshaker handShaker = WebSocketClientHandshakerFactory.newHandshaker(
                    new URI(strUri),
                    WebSocketVersion.V13,
                    null,
                    true,
                    new DefaultHttpHeaders()
            );

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            final ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpClientCodec());
                            pipeline.addLast(new HttpObjectAggregator(65535));
                            pipeline.addLast(new WebSocketClientProtocolHandler(handShaker));
                            //pipeline.addLast(new InternalServerMsgHandler());
                            pipeline.addLast(new MyInHandler());
                        }
                    });
            final ChannelFuture sync = bootstrap.connect(host, port).sync();
            if (!sync.isSuccess()){
                return;
            }
            log.info("连接到 gameServer, URI = {}",strUri);

            _gameServerCh = sync.channel();

            //对关闭通道进行监听,关闭事件触发后
            sync.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

    /**
     * 服务端 010
     * 游戏地址http://cdn0001.afrxvk.cn/hero_story/demo/step030/index.html?serverAddr=127.0.0.1:8001&userId=1
     */
    private static void serverMode(int port) throws Exception{
        // 线程组 一个Netty服务端启动时，通常会有两个NioEventLoopGroup：
        // 一个是监听线程组，主要是监听客户端请求，另一个是工作线程组，主要是处理与客户端的数据通讯
        //NioEventLoopGroup可以理解为一个线程池，内部维护了一组线程，都是无限循环
        //每个线程负责处理多个Channel上的事件，而一个Channel只对应于一个线程，这样可以回避多线程下的数据同步问题
        //默认是NettyRuntime.availableProcessors() * 2 当前CUP核数 * 2
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);

        GameMsgRecognizer.init();

        try {
            //创建服务器端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,2)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new LoggingHandler(LogLevel.ERROR))
                    .childHandler(new MyChannelInitializer());
            //启动服务器并且绑定端口,且是同步的
            final ChannelFuture sync = bootstrap.bind(port).sync();
            //监听端口是否绑定成功
            sync.addListener((future) -> {
                if (sync.isSuccess()){
                    latch.countDown();
                    log.info("监听端口8001绑定成功");
                }
            });
            //对关闭通道进行监听,关闭事件触发后
            sync.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void register() throws Exception{
        Properties properties = new Properties();
        properties.setProperty("serverAddr","127.0.0.1:8848");

        NamingService service = NamingFactory.createNamingService(properties);
        service.registerInstance("nettyServer","127.0.0.1",8001);
    }
}
