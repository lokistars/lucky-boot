package com.lucky.platform.server.handler;

import com.lucky.platform.entity.User;
import com.lucky.platform.server.protocolBuf.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @program: lucky-module-service
 * @description: 用户登录
 * @author: Loki
 * @data: 2023-02-22 13:46
 **/
public class UserLonginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {

    static private final Logger LOG = LoggerFactory.getLogger(UserLonginCmdHandler.class);


    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd userLoginCmd) {
        if (null == ctx || null == userLoginCmd) {
            return;
        }

        User user = new User(userLoginCmd.getUserName(), userLoginCmd.getPassword());

        GameMsgProtocol.UserLoginResult.Builder resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();
        if (Objects.isNull(user)){
            resultBuilder.setUserId(-1);
            resultBuilder.setUserName(userLoginCmd.getUserName());
            resultBuilder.setHeroAvatar("");
        }else{
            resultBuilder.setUserId(1);
            resultBuilder.setUserName(user.getUserName());
            resultBuilder.setHeroAvatar("");
        }
        GameMsgProtocol.UserLoginResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
