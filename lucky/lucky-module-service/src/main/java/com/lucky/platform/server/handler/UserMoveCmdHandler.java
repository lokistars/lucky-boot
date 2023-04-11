package com.lucky.platform.server.handler;

import com.lucky.platform.config.RedisConfig;
import com.lucky.platform.entity.MoveState;
import com.lucky.platform.entity.User;
import com.lucky.platform.server.factory.GameMsgHandlerContext;
import com.lucky.platform.server.protocolBuf.GameMsgProtocol;
import com.lucky.platform.utils.ChannelGroupUtils;
import org.redisson.api.RBucket;

/**
 * @program: lucky
 * @description: 用户移动
 * @author: Loki
 * @data: 2023-03-10 08:56
 **/
public class UserMoveCmdHandler  implements ICmdHandler<GameMsgProtocol.UserMoveToCmd>{


    @Override
    public void handle(GameMsgHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
            GameMsgProtocol.UserMoveToResult.Builder
                builder = GameMsgProtocol.UserMoveToResult.newBuilder();

        builder.setMoveUserId(ctx.getUserId());

        RBucket<User> bucket = RedisConfig.getInstance().getBucket("user_"+ctx.getUserId());
        User user = bucket.get();
        MoveState moveState = new MoveState();
        moveState.setFromPosX(cmd.getMoveFromPosX());
        moveState.setFromPosY(cmd.getMoveFromPosY());
        moveState.setToPosX(cmd.getMoveToPosX());
        moveState.setToPosY(cmd.getMoveToPosY());
        moveState.setStartTime(System.currentTimeMillis());
        user.setMoveState(moveState);
        bucket.set(user);

        // 移动位置
        builder.setMoveToPosX(moveState.getToPosX());
        builder.setMoveToPosY(moveState.getToPosY());

        builder.setMoveFromPosX(moveState.getFromPosX());
        builder.setMoveFromPosY(moveState.getFromPosY());
        builder.setMoveStartTime(moveState.getStartTime());
        GameMsgProtocol.UserMoveToResult build = builder.build();
        ChannelGroupUtils.send(build);
    }
}
