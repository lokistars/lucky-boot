package com.lucky.platform.server.handler;

import com.lucky.platform.config.RedisConfig;
import com.lucky.platform.entity.User;
import com.lucky.platform.server.factory.GameMsgHandlerContext;
import com.lucky.platform.server.protocolBuf.GameMsgProtocol;
import com.lucky.platform.utils.ChannelGroupUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @program: lucky-module-service
 * @description: 用户接入
 * @author: Loki
 * @data: 2023-02-25 13:46
 **/
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd>{
    static private final Logger LOG = LoggerFactory.getLogger(UserEntryCmdHandler.class);

    @Override
    public void handle(GameMsgHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }


        GameMsgProtocol.UserEntryResult.Builder builder = GameMsgProtocol.UserEntryResult.newBuilder();
        builder.setUserId(ctx.getUserId());
        String s = (ctx.getUserId() & 1) == 1 ? "Hero_Hammer" : "Hero_Skeleton";
        builder.setUserName(s);
        builder.setHeroAvatar("loki");
        GameMsgProtocol.UserEntryResult build = builder.build();

        User user = new User();
        user.setUserId(ctx.getUserId());
        user.setUserName(s);
        user.setHeroAvatar(s);
        RedisConfig.getInstance().getBucket("user_"+ctx.getUserId()).set(user,1000, TimeUnit.SECONDS);

        LOG.info("接入成功: {}",builder.getUserId());
        ChannelGroupUtils.send(build);
    }
}
