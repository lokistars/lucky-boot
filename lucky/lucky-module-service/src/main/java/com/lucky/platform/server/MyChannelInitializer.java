package com.lucky.platform.server;

import com.lucky.platform.server.handler.GameMsgHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * @author: Loki
 * @data: 2021-10-06 15:34
 **/
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //获取管道向管道加入处理器
        final ChannelPipeline pipeline = ch.pipeline();
        // 加入一个netty提供的httpServerCodec codec => [coder - decoder]
        pipeline.addLast(new HttpServerCodec());
        // netty提供的处理器，用于处理http的数据在传输过程中是分段的，HttpObjectAggregator可以将多个段聚合
        pipeline.addLast(new HttpObjectAggregator(65535));
        // netty提供的处理器，用于处理websocket，数据以帧的形式传递
        pipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));
        //pipeline.addLast(new ClientMsgHandler());

        // 编码器
        pipeline.addLast(new MessageEncoder());

        pipeline.addLast(new MessageDecoder());
        pipeline.addLast(new GameMsgHandler());


        // 读空闲，写空闲，读写空闲
        // pipeline.addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS));
    }
}
