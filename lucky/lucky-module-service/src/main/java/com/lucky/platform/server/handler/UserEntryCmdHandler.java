package com.lucky.platform.server.handler;

import com.lucky.platform.server.protocolBuf.GameMsgProtocol;
import com.lucky.platform.utils.ChannelGroupUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: lucky-module-service
 * @description: 用户接入
 * @author: Loki
 * @data: 2023-02-25 13:46
 **/
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd>{
    static private final Logger LOG = LoggerFactory.getLogger(UserEntryCmdHandler.class);



    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }
        LOG.info("接入成功");
        GameMsgProtocol.UserEntryResult.Builder builder = GameMsgProtocol.UserEntryResult.newBuilder();
        builder.setUserId(1);
        builder.setHeroAvatar("A");
        GameMsgProtocol.UserEntryResult build = builder.build();
        ChannelGroupUtils.send(build);
    }
}
