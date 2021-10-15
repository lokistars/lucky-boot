package com.lucky.platform.ioDemo.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author: Loki
 * @data: 2021-10-06 15:34
 **/
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //获取管道向管道加入处理器
        final ChannelPipeline pipeline = ch.pipeline();

        //添加自定义的Handler
        //pipeline.addLast(new MyInHandler());
        //pipeline.addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS));

        //pipeline.addLast(new HttpObjectAggregator(1024));
        //pipeline.addLast(new ChunkedWriteHandler());
        //pipeline.addLast(new WebSocketServerProtocolHandler("/"));
        addLastWebSocket(pipeline);
    }

    /**
     * WebSocket 服务端 编解码器
     *
     * @param pi
     */
    private void addLastWebSocket(ChannelPipeline pi) {

        pi.addLast(
                new HttpServerCodec(),
                new HttpObjectAggregator(65535),
                new WebSocketServerProtocolHandler("/websocket"),
                new MyHttpHandler()
        );
    }
}
