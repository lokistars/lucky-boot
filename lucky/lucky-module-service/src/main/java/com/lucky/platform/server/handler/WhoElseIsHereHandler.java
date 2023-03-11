package com.lucky.platform.server.handler;

import com.lucky.platform.entity.User;
import com.lucky.platform.server.protocolBuf.GameMsgProtocol;
import com.lucky.platform.utils.ChannelGroupUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * @program: lucky-module-service
 * @description: 还有谁在场
 * @author: Loki
 * @data: 2023-02-25 00:55
 **/
public class WhoElseIsHereHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {

    static private final Logger LOG = LoggerFactory.getLogger(UserEntryCmdHandler.class);
    static boolean type = true;
    @Override
    public void handle(GameMsgHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
        GameMsgProtocol.WhoElseIsHereResult.Builder
                builder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();
        // 只有登录后才可以拿到
        List<User> users = ChannelGroupUtils.listUser();
        for (User user : users) {

            int id = user.getUserId();

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder
                    userinfo = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            userinfo.setUserId(id);
            userinfo.setUserName(user.getUserName());
            userinfo.setHeroAvatar(user.getHeroAvatar());

            // 设置移动坐标
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder
                    mvStateBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();

            if (Objects.nonNull(user.getMoveState())){
                mvStateBuilder.setFromPosX(user.getMoveState().getFromPosX());
                mvStateBuilder.setFromPosY(user.getMoveState().getFromPosY());
                mvStateBuilder.setToPosX(user.getMoveState().getToPosX());
                mvStateBuilder.setToPosY(user.getMoveState().getToPosY());
            }

            userinfo.setMoveState(mvStateBuilder);

            GameMsgProtocol.WhoElseIsHereResult.UserInfo build = userinfo.build();
            builder.addUserInfo(build);
        }
        GameMsgProtocol.WhoElseIsHereResult build = builder.build();
        ctx.writeAndFlush(build);
    }
}
