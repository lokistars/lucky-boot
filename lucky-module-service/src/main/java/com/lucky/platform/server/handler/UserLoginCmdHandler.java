package com.lucky.platform.server.handler;

import com.lucky.platform.entity.User;
import com.lucky.platform.server.factory.GameMsgHandlerContext;
import com.lucky.platform.server.protocolBuf.GameMsgProtocol;
import com.lucky.platform.utils.ChannelGroupUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: lucky-module-service
 * @description: 用户登录
 * @author: Loki
 * @data: 2023-02-22 13:46
 **/
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {

    static private final Logger LOG = LoggerFactory.getLogger(UserLoginCmdHandler.class);

    @Override
    public void handle(GameMsgHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }
        LOG.info("登录成功:{}", cmd.getUserName());
        User user = ChannelGroupUtils.addUser(ctx.getUserId(), cmd.getUserName(), cmd.getPassword());

        GameMsgProtocol.UserLoginResult.Builder resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();

        resultBuilder.setUserId(ctx.getUserId());
        resultBuilder.setUserName(user.getUserName());
        resultBuilder.setHeroAvatar(user.getHeroAvatar());

        GameMsgProtocol.UserLoginResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
