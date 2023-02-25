package com.lucky.platform.server.handler;

import com.lucky.platform.server.protocolBuf.GameMsgProtocol;
import com.lucky.platform.utils.ChannelGroupUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: lucky-module-service
 * @description:
 * @author: Loki
 * @data: 2023-02-25 00:55
 **/
public class WhoElseIsHereHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {

    static private final Logger LOG = LoggerFactory.getLogger(UserEntryCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd whoElseIsHereCmd) {
        GameMsgProtocol.WhoElseIsHereResult.Builder builder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        GameMsgProtocol.WhoElseIsHereResult build = builder.build();
        ctx.writeAndFlush(build);
    }
}
