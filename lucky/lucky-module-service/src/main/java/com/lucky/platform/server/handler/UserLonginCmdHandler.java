package com.lucky.platform.server.handler;

import com.lucky.platform.entity.User;
import com.lucky.platform.server.protocolBuf.GameMsgProtocol;
import com.lucky.platform.utils.ChannelGroupUtils;
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
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }
        LOG.info("登录成功");
        User user = new User(cmd.getUserName(), cmd.getPassword());

        GameMsgProtocol.UserLoginResult.Builder resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();
        if (Objects.isNull(user)){
            resultBuilder.setUserId(-1);
            resultBuilder.setUserName(cmd.getUserName());
            resultBuilder.setHeroAvatar("");
        }else{
            resultBuilder.setUserId(2);
            resultBuilder.setUserName(user.getUserName());
            resultBuilder.setHeroAvatar("");
        }
        user.setUserId(1);
        ChannelGroupUtils.addUser(user);
        GameMsgProtocol.UserLoginResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
