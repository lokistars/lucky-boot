package com.lucky.platform.server.handler;

import com.google.protobuf.GeneratedMessageV3;
import com.lucky.platform.server.factory.GameMsgHandlerContext;

/**
 * @program: lucky-module-service
 * @description:
 * @author: Loki
 * @data: 2023-02-22 13:44
 **/
public interface ICmdHandler<TCmd extends GeneratedMessageV3> {

    /**
     * 消息处理
     * @param ctx
     * @param cmd
     */
    void handle(GameMsgHandlerContext ctx, TCmd cmd);
}
